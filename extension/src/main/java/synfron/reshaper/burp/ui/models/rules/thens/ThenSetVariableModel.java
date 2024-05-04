package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSetVariable;
import synfron.reshaper.burp.core.vars.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenSetVariableModel extends ThenSetModel<ThenSetVariableModel, ThenSetVariable> implements IVariableCreator {

    @Getter
    private VariableSource targetSource;
    @Getter
    private String variableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;

    public ThenSetVariableModel(ProtocolType protocolType, ThenSetVariable then, Boolean isNew) {
        super(protocolType, then, isNew);
        targetSource = then.getTargetSource();
        variableName = VariableString.toString(then.getVariableName(), variableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
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

    public void setItemPlacement(SetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
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
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setTargetSource(targetSource);
        ruleOperation.setVariableName(VariableString.getAsVariableString(variableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        return super.persist();
    }

    @Override
    protected String getTargetName() {
        return VariableSourceEntry.getShortTag(targetSource, variableName);
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
