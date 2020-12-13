package synfron.reshaper.burp.core.rules.thens.entities.script;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.Objects;

public class VariablesObj {
    private final EventInfo eventInfo;

    public VariablesObj(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public String getGlobalVariable(String name) {
        return getVariable(GlobalVariables.get(), name);
    }

    public String getEventVariable(String name) {
        return getVariable(eventInfo.getVariables(), name);
    }

    private String getVariable(Variables variables, String name) {
        Variable variable = variables.getOrDefault(name);
        return variable != null ?
                Objects.toString(variable.getValue()) :
                null;
    }

    public void setGlobalVariable(String name, String value) {
        if (!VariableString.isValidVariableName(name)) {
            throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
        }
        GlobalVariables.get().add(name).setValue(value);
    }

    public void setEventVariable(String name, String value) {
        if (!VariableString.isValidVariableName(name)) {
            throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
        }
        eventInfo.getVariables().add(name).setValue(value);
    }

    public void deleteGlobalVariable(String name) {
        GlobalVariables.get().remove(name);
    }

    public void deleteEventVariable(String name) {
        eventInfo.getVariables().remove(name);
    }
}
