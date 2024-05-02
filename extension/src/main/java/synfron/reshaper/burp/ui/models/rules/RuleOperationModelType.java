package synfron.reshaper.burp.ui.models.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;

@AllArgsConstructor
public class RuleOperationModelType<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> {
    @Getter
    private final Class<P> type;
    @Getter
    private final RuleOperationType<T> ruleOperationType;
    @Getter
    private final boolean isDefault;

    protected RuleOperationModelType(Class<P> type, RuleOperationType<T> ruleOperationType) {
        this.type = type;
        this.ruleOperationType = ruleOperationType;
        isDefault = false;
    }

    public String getName() {
        return ruleOperationType.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean hasProtocolType(ProtocolType protocolType) {
        return switch (protocolType) {
            case Any -> true;
            case Http -> IHttpRuleOperation.class.isAssignableFrom(ruleOperationType.getType());
            case WebSocket -> IWebSocketRuleOperation.class.isAssignableFrom(ruleOperationType.getType());
        };
    }
}
