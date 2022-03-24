package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final List<String> variableNames;

    @Getter
    private String variableName;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public GlobalVariableTagWizardModel(List<VariableSourceEntry> entries) {
        variableNames = Stream.concat(
                        GlobalVariables.get().getValues().stream().map(Variable::getName),
                        entries.stream()
                                .filter(entry -> entry.getVariableSource() == VariableSource.Global)
                                .map(VariableSourceEntry::getName)
                )
                .filter(VariableString::isValidVariableName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        variableName = variableNames.stream().findFirst().orElse(null);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        propertyChanged("variableName", variableName);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(variableName)) {
            errors.add("Variable Name is required");
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Global;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(VariableSource.Global, variableName) :
                null;
    }
}
