package synfron.reshaper.burp.core.rules;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Rule implements Serializable {
    @Getter
    private final transient PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter @Setter
    private List<When<?>> whens = new ArrayList<>();
    @Getter @Setter
    private List<Then<?>> thens = new ArrayList<>();
    @Getter
    private boolean enabled = true;
    @Getter
    private boolean autoRun = true;
    @Getter
    private String name;

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

    @Override
    public String toString() {
        return name;
    }

    public Rule withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public Rule copy() {
        Rule rule = new Rule();
        rule.whens = whens.stream().map(IRuleOperation::copy).map(when -> (When<?>)when).collect(Collectors.toList());
        rule.thens = thens.stream().map(IRuleOperation::copy).map(then -> (Then<?>)then).collect(Collectors.toList());
        rule.autoRun = autoRun;
        rule.enabled = false;
        return rule;
    }
}
