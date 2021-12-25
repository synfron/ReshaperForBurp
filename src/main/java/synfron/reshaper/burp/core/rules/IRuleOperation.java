package synfron.reshaper.burp.core.rules;

import java.io.Serializable;

public interface IRuleOperation<T extends IRuleOperation<T>> extends Serializable {
    RuleOperationType<T> getType();

    T copy();
}
