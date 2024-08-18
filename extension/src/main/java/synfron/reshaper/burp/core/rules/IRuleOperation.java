package synfron.reshaper.burp.core.rules;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.Variables;

import java.io.Serializable;

public interface IRuleOperation<T extends IRuleOperation<T>> extends Serializable {
    RuleOperationType<T> getType();

    IRuleOperation<?> copy();

    default boolean isGroup() {
        return false;
    }

    default int groupSize() {
        return 0;
    }

    default Variables getVariables(VariableSource variableSource, EventInfo eventInfo) {
        return switch (variableSource) {
            case Event, EventList -> eventInfo.getVariables();
            case Global, GlobalList -> eventInfo.getWorkspace().getGlobalVariables();
            case Session, SessionList -> eventInfo.getSessionVariables();
            default -> null;
        };
    }
}
