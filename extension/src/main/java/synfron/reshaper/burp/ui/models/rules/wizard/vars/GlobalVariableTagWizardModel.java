package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceDependent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalVariableTagWizardModel extends CustomVariableTagWizardModel implements IWorkspaceDependent {

    public GlobalVariableTagWizardModel(List<VariableSourceEntry> entries) {
        super(entries);
    }

    @Override
    public List<String> getUpdatedVariableNames(List<VariableSourceEntry> entries) {
        return Stream.concat(
                        getHostedWorkspace().getGlobalVariables().getValues().stream().filter(variable -> !variable.isList()).map(Variable::getName),
                        entries.stream()
                                .filter(entry -> entry.getVariableSource() == VariableSource.Global)
                                .map(entry -> entry.getParams().getFirst())
                )
                .filter(VariableString::isValidVariableName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Global;
    }
}
