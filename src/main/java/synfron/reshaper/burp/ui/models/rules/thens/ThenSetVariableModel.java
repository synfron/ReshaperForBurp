package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSetVariable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenSetVariableModel extends ThenSetModel<ThenSetVariableModel, ThenSetVariable> implements IVariableCreator {

    @Getter
    private VariableSource targetSource;
    @Getter
    private String variableName;

    public ThenSetVariableModel(ProtocolType protocolType, ThenSetVariable then, Boolean isNew) {
        super(protocolType, then, isNew);
        targetSource = then.getTargetSource();
        variableName = VariableString.toString(then.getVariableName(), variableName);
        VariableCreatorRegistry.register(this);
    }

    public void setTargetSource(VariableSource targetSource) {
        this.targetSource = targetSource;
        propertyChanged("targetSource", targetSource);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        propertyChanged("destinationVariableName", variableName);
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
        return super.persist();
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
    public RuleOperationModelType<ThenSetVariableModel, ThenSetVariable> getType() {
        return ThenModelType.SetVariable;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(variableName) ?
                List.of(new VariableSourceEntry(targetSource, variableName)) :
                Collections.emptyList();
    }
}
