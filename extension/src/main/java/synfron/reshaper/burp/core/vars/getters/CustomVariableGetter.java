package synfron.reshaper.burp.core.vars.getters;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.Variables;

public class CustomVariableGetter extends VariableGetter {

    @Override
    public String getText(VariableSourceEntry variable, EventInfo eventInfo) {
        Variable value = switch (variable.getVariableSource()) {
            case Global -> GlobalVariables.get().getOrDefault(Variables.asKey(variable.getName(), false));
            case Event -> eventInfo.getVariables().getOrDefault(Variables.asKey(variable.getName(), false));
            case Session -> eventInfo.getSessionVariables().getOrDefault(Variables.asKey(variable.getName(), false));
            default -> null;
        };
        return value != null ? TextUtils.toString(value.getValue()) : null;
    }
}
