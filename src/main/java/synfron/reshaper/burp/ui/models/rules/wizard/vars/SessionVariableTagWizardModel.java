package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final List<String> variableNames;

    @Getter
    private String variableName;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public SessionVariableTagWizardModel(List<VariableSourceEntry> entries) {
        variableNames = entries.stream()
                .filter(entry -> entry.getVariableSource() == VariableSource.Session)
                .map(VariableSourceEntry::getName)
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
        return VariableSource.Session;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(VariableSource.Session, variableName) :
                null;
    }
}
