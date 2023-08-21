package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;
import java.util.stream.Collectors;

public class SessionVariableTagWizardModel extends CustomVariableTagWizardModel {

    public SessionVariableTagWizardModel(List<VariableSourceEntry> entries) {
        super(entries);
    }

    @Override
    public List<String> getUpdatedVariableNames(List<VariableSourceEntry> entries) {
        return entries.stream()
                .filter(entry -> entry.getVariableSource() == VariableSource.Session)
                .map(VariableSourceEntry::getName)
                .filter(VariableString::isValidVariableName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Session;
    }
}
