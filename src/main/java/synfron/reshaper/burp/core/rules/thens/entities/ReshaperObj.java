package synfron.reshaper.burp.core.rules.thens.entities;

import synfron.reshaper.burp.core.messages.EventInfo;

public class ReshaperObj {
    public VariablesObj variables;

    public ReshaperObj(EventInfo eventInfo) {
        this.variables = new VariablesObj(eventInfo);
    }
}
