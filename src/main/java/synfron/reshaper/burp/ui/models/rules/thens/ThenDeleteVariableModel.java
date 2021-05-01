package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteVariable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenDeleteVariableModel extends ThenModel<ThenDeleteVariableModel, ThenDeleteVariable> {

    @Getter
    private VariableSource targetSource;
    @Getter
    private String variableName;

    public ThenDeleteVariableModel(ThenDeleteVariable then, Boolean isNew) {
        super(then, isNew);
        targetSource = then.getTargetSource();
        variableName = VariableString.getFormattedString(then.getVariableName(), variableName);
    }

    public void setTargetSource(VariableSource targetSource) {
        this.targetSource = targetSource;
        propertyChanged("targetSource", targetSource);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        propertyChanged("variableName", variableName);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(variableName)) {
            errors.add("Variable Name is required");
        } else if (!VariableString.isValidVariableName(variableName)) {
            errors.add("Variable Name is invalid");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setTargetSource(targetSource);
        ruleOperation.setVariableName(VariableString.getAsVariableString(variableName));
        setValidated(true);
        return true;
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    public RuleOperationModelType<ThenDeleteVariableModel, ThenDeleteVariable> getType() {
        return ThenModelType.DeleteVariable;
    }
}
