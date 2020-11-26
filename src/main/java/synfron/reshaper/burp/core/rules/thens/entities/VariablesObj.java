package synfron.reshaper.burp.core.rules.thens.entities;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.vars.GlobalVariables;
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
        return variables.has(name) ?
                Objects.toString(variables.get(name).getValue()) :
                null;
    }

    public void setGlobalVariable(String name, String value) {
        GlobalVariables.get().add(name).setValue(value);
    }

    public void setEventVariable(String name, String value) {
        eventInfo.getVariables().add(name).setValue(value);
    }

    public void deleteGlobalVariable(String name) {
        GlobalVariables.get().remove(name);
    }

    public void deleteEventVariable(String name) {
        eventInfo.getVariables().remove(name);
    }
}
