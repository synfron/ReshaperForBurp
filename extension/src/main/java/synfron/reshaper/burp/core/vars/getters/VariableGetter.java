package synfron.reshaper.burp.core.vars.getters;

import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

public abstract class VariableGetter {

    public abstract String getText(VariableSourceEntry variable, EventInfo eventInfo);
}
