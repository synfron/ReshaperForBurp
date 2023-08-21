package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteVariable;
import synfron.reshaper.burp.core.vars.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenDeleteVariableModel extends ThenModel<ThenDeleteVariableModel, ThenDeleteVariable> {

    @Getter
    private VariableSource targetSource;
    @Getter
    private String variableName;
    @Getter
    private DeleteListItemPlacement itemPlacement;
    @Getter
    private String index;

    public ThenDeleteVariableModel(ProtocolType protocolType, ThenDeleteVariable then, Boolean isNew) {
        super(protocolType, then, isNew);
        targetSource = then.getTargetSource();
        variableName = VariableString.toString(then.getVariableName(), variableName);
        itemPlacement = then.getItemPlacement();
        index = VariableString.toString(then.getIndex(), index);
    }

    public void setTargetSource(VariableSource targetSource) {
        this.targetSource = targetSource;
        propertyChanged("targetSource", targetSource);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        propertyChanged("variableName", variableName);
    }

    public void setItemPlacement(DeleteListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(variableName)) {
            errors.add("Variable Name is required");
        } else if (!VariableString.isValidVariableName(variableName)) {
            errors.add("Variable Name is invalid");
        }
        if (targetSource.isList() && itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setTargetSource(targetSource);
        ruleOperation.setVariableName(VariableString.getAsVariableString(variableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
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
    protected String getTargetName() {
        return abbreviateTargetName(VariableSourceEntry.getShortTag(targetSource, abbreviateTargetName(variableName)));
    }

    @Override
    public RuleOperationModelType<ThenDeleteVariableModel, ThenDeleteVariable> getType() {
        return ThenModelType.DeleteVariable;
    }
}
