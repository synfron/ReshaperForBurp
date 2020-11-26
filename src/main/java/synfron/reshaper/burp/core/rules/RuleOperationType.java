package synfron.reshaper.burp.core.rules;

import lombok.Getter;

public abstract class RuleOperationType<T extends IRuleOperation<T>> {
    @Getter
    private final String name;
    @Getter
    private final Class<T> type;

    protected RuleOperationType(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
