package synfron.reshaper.burp.core.rules;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.core.utils.Serializer;

import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {
    @Getter
    private final transient PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    @Getter
    private List<? extends When<?>> whens = List.of();
    @Getter
    private List<? extends Then<?>> thens = List.of();
    @Getter
    private boolean enabled = true;
    @Getter
    private boolean autoRun = true;
    @Getter
    private String name;
    @Getter
    private transient boolean diagnosticsEnabled;

    public void setName(String name) {
        this.name = name;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "name", name));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "enabled", enabled));
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "autoRun", autoRun));
    }

    public void setWhens(List<? extends When<?>> whens) {
        this.whens = whens;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "whens", whens));
    }

    public void setThens(List<? extends Then<?>> thens) {
        this.thens = thens;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "thens", thens));
    }

    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) {
        this.diagnosticsEnabled = diagnosticsEnabled;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "diagnosticsEnabled", diagnosticsEnabled));
    }

    @Override
    public String toString() {
        return name;
    }

    public Rule withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public Rule copy() {
        Rule rule = Serializer.copy(this);
        rule.name = null;
        rule.enabled = false;
        return rule;
    }
}
