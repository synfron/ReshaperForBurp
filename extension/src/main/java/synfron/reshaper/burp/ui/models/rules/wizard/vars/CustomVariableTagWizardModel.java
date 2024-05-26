package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableTag;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private final List<String> variableNames;

    @Getter
    private String variableName;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public CustomVariableTagWizardModel(List<VariableSourceEntry> entries) {
        variableNames = getUpdatedVariableNames(entries);
        variableName = variableNames.stream().findFirst().orElse(null);
    }

    protected void propertyChanged(String name, Object value) {
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

    public abstract List<String> getUpdatedVariableNames(List<VariableSourceEntry> entries);

    public abstract VariableSource getVariableSource();

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableTag.getShortTag(getVariableSource(), variableName) :
                null;
    }
}
