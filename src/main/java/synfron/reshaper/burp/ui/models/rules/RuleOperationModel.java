package synfron.reshaper.burp.ui.models.rules;

import lombok.Getter;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.IRuleOperation;

import java.util.ArrayList;
import java.util.List;

public abstract class RuleOperationModel<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> {
    @Getter
    protected final T ruleOperation;
    protected boolean saved;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public RuleOperationModel(T ruleOperation, boolean isNew) {
        this.ruleOperation = ruleOperation;
        this.saved = !isNew;
    }

    public List<String> validate() {
        return new ArrayList<>();
    }

    protected void setSaved(boolean saved) {
        if (saved != this.saved) {
            this.saved = saved;
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "saved", saved));
        }
    }

    protected void propertyChanged(String name, Object value) {
        setSaved(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public abstract boolean persist();

    public abstract boolean record();

    @Override
    public String toString() {
        return ruleOperation.getType().getName() + (saved ? "" : " *");
    }

    public abstract RuleOperationModelType<P, T> getType();
}
