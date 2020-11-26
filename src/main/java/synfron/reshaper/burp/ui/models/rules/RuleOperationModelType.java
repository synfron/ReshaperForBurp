package synfron.reshaper.burp.ui.models.rules;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class RuleOperationModelType<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> {
    @Getter
    private final String name;
    @Getter
    private final Class<P> type;
    @Getter
    private final RuleOperationType<T> ruleOperationType;

    protected RuleOperationModelType(String name, Class<P> type, RuleOperationType<T> ruleOperationType) {
        this.name = name;
        this.type = type;
        this.ruleOperationType = ruleOperationType;
    }

    @Override
    public String toString() {
        return name;
    }
}
