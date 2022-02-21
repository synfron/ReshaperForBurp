package synfron.reshaper.burp.ui.models.rules.wizard;

import burp.BurpExtender;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.core.rules.whens.WhenHasEntity;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class WhenWizardModel {
    @Getter
    private final IEventInfo eventInfo;
    @Getter
    private String ruleName;
    @Getter
    private final List<WhenWizardItemModel> items = new ArrayList<>();

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter
    private boolean invalidated;

    public WhenWizardModel(IEventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public void resetPropertyChangedListener() {
        propertyChangedEvent.clearListeners();
        items.forEach(item -> item.getPropertyChangedEvent().clearListeners());
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
        propertyChanged("ruleName", ruleName);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public WhenWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(ruleName)) {
            errors.add("Rule Name is required");
        }
        errors.addAll(items.stream().flatMap(item -> item.validate().stream()).collect(Collectors.toList()));
        return errors;
    }

    public WhenWizardItemModel addItem() {
        WhenWizardItemModel item = new WhenWizardItemModel(MessageValue.Url, eventInfo);
        items.add(item);
        propertyChanged("items", items);
        return item;
    }

    private WhenHasEntity toHasEntity(WhenWizardItemModel item) {
        WhenHasEntity when = new WhenHasEntity();
        when.setMessageValue(item.getMessageValue());
        when.setIdentifier(VariableString.getAsVariableString(item.getIdentifiers().getSelectedOption(), false));
        return when;
    }

    private WhenMatchesText toMatchesText(WhenWizardItemModel item) {
        WhenMatchesText when = new WhenMatchesText();
        when.setMatchType(MatchType.valueOf(item.getMatchType().name()));
        when.setMessageValue(item.getMessageValue());
        when.setIdentifier(VariableString.getAsVariableString(item.getIdentifiers().getSelectedOption(), false));
        when.setIdentifierPlacement(GetItemPlacement.Last);
        when.setUseMessageValue(true);
        when.setMatchText(VariableString.getAsVariableString(item.getText()));
        return when;
    }

    public int removeItem(WhenWizardItemModel whenWizardItemModel) {
        int index = items.indexOf(whenWizardItemModel);
        items.remove(index);
        propertyChanged("items", items);
        return index;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    public boolean createRule() {
        if (validate().isEmpty()) {
            Rule rule = new Rule();
            LinkedList<When<?>> whens = new LinkedList<>();
            boolean requiresResponse = false;
            for (WhenWizardItemModel item : items) {
                requiresResponse |= item.requiresResponse();
                if (item.getMatchType().isMatcher()) {
                    WhenMatchesText when = toMatchesText(item);
                    whens.addLast(when);
                } else {
                    WhenHasEntity when = toHasEntity(item);
                    whens.addLast(when);
                }
            }
            WhenEventDirection direction = new WhenEventDirection();
            direction.setDataDirection(requiresResponse ? DataDirection.Response : DataDirection.Request);
            whens.addFirst(direction);
            rule.setName(ruleName);
            rule.setEnabled(false);
            rule.setAutoRun(true);
            rule.setWhens(whens.toArray(When[]::new));
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().addRule(rule);
            setInvalidated(false);
            return true;
        }
        setInvalidated(true);
        return false;
    }
}
