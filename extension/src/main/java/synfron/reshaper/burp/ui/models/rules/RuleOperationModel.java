package synfron.reshaper.burp.ui.models.rules;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.IRuleOperation;

import java.util.ArrayList;
import java.util.List;

public abstract class RuleOperationModel<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> {
    @Getter
    protected final ProtocolType protocolType;
    @Getter
    protected final T ruleOperation;
    @Getter
    protected boolean validated;
    private String validatedTarget;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public RuleOperationModel(ProtocolType protocolType, T ruleOperation, boolean isNew) {
        this.protocolType = protocolType;
        this.ruleOperation = ruleOperation;
        this.validated = !isNew;
    }

    public List<String> validate() {
        return new ArrayList<>();
    }

    protected void setValidated(boolean validated) {
        if (validated != this.validated) {
            this.validated = validated;
            resolveValidatedTarget();
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "validated", validated));
        }
    }

    protected void propertyChanged(String name, Object value) {
        setValidated(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public abstract boolean persist();

    protected String getTargetName() {
        return "";
    }

    protected String abbreviateTargetName(String text) {
        return StringUtils.abbreviate(StringUtils.defaultString(text), 36);
    }

    private String resolveValidatedTarget() {
        if (validated) {
            try {
                validatedTarget = getTargetName();
            } catch (Exception ignored) {
                validatedTarget = "";
            }
        } else {
            validatedTarget = "";
        }
        return validatedTarget;
    }

    @Override
    public String toString() {
        String target = resolveValidatedTarget();
        target = StringUtils.isEmpty(target) ? "" : ": " + target;
        return ruleOperation.getType().getName() + target + (validated ? "" : " *");
    }

    public abstract RuleOperationModelType<P, T> getType();
}
