package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import synfron.reshaper.burp.core.vars.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalListVariableTagWizardModel extends CustomListVariableTagWizardModel {

    public GlobalListVariableTagWizardModel(List<VariableSourceEntry> entries) {
        super(entries);
    }

    @Override
    public List<String> getUpdatedVariableNames(List<VariableSourceEntry> entries) {
        return Stream.concat(
                        GlobalVariables.get().getValues().stream().filter(Variable::isList).map(Variable::getName),
                        entries.stream()
                                .filter(entry -> entry.getVariableSource() == VariableSource.GlobalList)
                                .map(entry -> entry.getParams().getFirst())
                )
                .filter(VariableString::isValidVariableName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.GlobalList;
    }
}
