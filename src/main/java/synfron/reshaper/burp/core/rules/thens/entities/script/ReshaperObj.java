package synfron.reshaper.burp.core.rules.thens.entities.script;

import synfron.reshaper.burp.core.messages.EventInfo;

public class ReshaperObj {
    public VariablesObj variables;
    public EventObj event;

    public ReshaperObj(EventInfo eventInfo) {
        this.variables = new VariablesObj(eventInfo);
        this.event = new EventObj(eventInfo);
    }
}
