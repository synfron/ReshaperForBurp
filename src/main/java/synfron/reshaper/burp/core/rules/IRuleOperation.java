package synfron.reshaper.burp.core.rules;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.Variables;

import java.io.Serializable;

public interface IRuleOperation<T extends IRuleOperation<T>> extends Serializable {
    RuleOperationType<T> getType();

    IRuleOperation<?> copy();
    default Variables getVariables(VariableSource variableSource, EventInfo eventInfo) {
        return switch (variableSource) {
            case Event -> eventInfo.getVariables();
            case Global -> GlobalVariables.get();
            case Session -> eventInfo instanceof WebSocketEventInfo<?> ? ((WebSocketEventInfo<?>)eventInfo).getSessionVariables() : null;
            default -> null;
        };
    }
}
