package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;
import java.util.stream.Collectors;

public class SessionListVariableTagWizardModel extends CustomListVariableTagWizardModel {

    public SessionListVariableTagWizardModel(List<VariableSourceEntry> entries) {
        super(entries);
    }

    @Override
    public List<String> getUpdatedVariableNames(List<VariableSourceEntry> entries) {
        return entries.stream()
                .filter(entry -> entry.getVariableSource() == VariableSource.SessionList)
                .map(entry -> entry.getParams().getFirst())
                .filter(VariableString::isValidVariableName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.SessionList;
    }
}
