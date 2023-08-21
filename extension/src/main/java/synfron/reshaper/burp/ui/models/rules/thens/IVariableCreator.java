package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.List;

public interface IVariableCreator {
    List<VariableSourceEntry> getVariableEntries();
}
