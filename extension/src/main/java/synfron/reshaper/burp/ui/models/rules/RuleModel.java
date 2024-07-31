package synfron.reshaper.burp.ui.models.rules;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleModel {
    @Getter
    private final Rule rule;
    @Getter
    private boolean saved;
    @Getter
    private boolean enabled;
    @Getter
    private boolean autoRun;
    @Getter
    private final ProtocolType protocolType;
    @Getter
    private boolean isNew;
    @Getter
    private String name;
    @Getter
    private final List<WhenModel<?,?>> whens;
    @Getter
    private final List<ThenModel<?,?>> thens;
    @Getter
    private boolean diagnosticsEnabled;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private final IEventListener<PropertyChangedArgs> ruleOperationChangedListener = this::onRuleOperationChanged;
    private final IEventListener<PropertyChangedArgs> ruleChangedListener = this::onRuleChanged;

    public RuleModel(ProtocolType protocolType, Rule rule) {
        this(protocolType, rule, false);
    }

    public RuleModel(ProtocolType protocolType, Rule rule, boolean isNew) {
        this.protocolType = protocolType;
        this.isNew = isNew;
        this.rule = rule;
        this.whens = rule.getWhens().stream()
                .map(when -> WhenModel.getModel(protocolType, when).withListener(ruleOperationChangedListener))
                .collect(Collectors.toList());
        this.thens = rule.getThens().stream()
                .map(then -> ThenModel.getModel(protocolType, then).withListener(ruleOperationChangedListener))
                .collect(Collectors.toList());
        this.name = rule.getName();
        this.enabled = rule.isEnabled();
        this.autoRun = rule.isAutoRun();
        this.diagnosticsEnabled = rule.isDiagnosticsEnabled();
        this.saved = !isNew;

        rule.withListener(ruleChangedListener);
    }

    private void onRuleOperationChanged(PropertyChangedArgs args) {
        if (args.getName().equals("saved") && !((boolean) args.getValue())) {
            setSaved(false);
        }
    }

    private void onRuleChanged(PropertyChangedArgs args) {
        if (args.getName().equals("diagnosticsEnabled")) {
            diagnosticsEnabled = rule.isDiagnosticsEnabled();
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "diagnosticsEnabled", diagnosticsEnabled));
        }
    }

    public RuleModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public void setName(String name) {
        this.name = name;
        propertyChanged("name", name);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        propertyChanged("enabled", enabled);
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
        propertyChanged("autoRun", autoRun);
    }

    public void addWhen(WhenModel<?,?> when) {
        whens.add(when.withListener(ruleOperationChangedListener));
        propertyChanged("whens", whens);
    }

    public void addThen(ThenModel<?,?> then) {
        thens.add(then.withListener(ruleOperationChangedListener));
        propertyChanged("thens", thens);
    }

    private void propertyChanged(String name, Object value) {
        setSaved(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setSaved(boolean saved) {
        if (saved != this.saved) {
            this.saved = saved;
            this.isNew = !saved && this.isNew;
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "saved", saved));
        }
    }

    public void markChanged() {
        setSaved(false);
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(name)) {
            errors.add("Rule Name is required");
        }
        errors.addAll(whens.stream()
                .flatMap(model -> model.validate().stream()
                        .map(error -> String.format("%s: %s", model.getRuleOperation().getType().getName(), error))
                ).toList()
        );
        errors.addAll(thens.stream()
                .flatMap(model -> model.validate().stream()
                        .map(error -> String.format("%s: %s", model.getRuleOperation().getType().getName(), error))
                ).toList()
        );
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        rule.setEnabled(false);

        whens.forEach(WhenModel::persist);
        thens.forEach(RuleOperationModel::persist);

        rule.setWhens(whens.stream()
                .map(model -> (When<?>)model.getRuleOperation())
                .toList()
        );

        rule.setThens(thens.stream()
                .map(model -> (Then<?>)model.getRuleOperation())
                .toList()
        );

        rule.setName(name);
        rule.setEnabled(enabled);
        rule.setAutoRun(autoRun);
        setSaved(true);
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.defaultIfEmpty(name, "untitled") + (saved ? "" : " *") + (diagnosticsEnabled ? " (Debug)" : "");
    }
}
