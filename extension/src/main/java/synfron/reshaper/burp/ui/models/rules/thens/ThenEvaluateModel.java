package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenEvaluate;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenEvaluateModel extends ThenModel<ThenEvaluateModel, ThenEvaluate> implements IVariableCreator {

    @Getter
    private String x;
    @Getter
    private Operation operation;
    @Getter
    private String y;
    @Getter
    private VariableSource destinationVariableSource;
    @Getter
    private String destinationVariableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;

    public ThenEvaluateModel(ProtocolType protocolType, ThenEvaluate then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.x = VariableString.toString(then.getX(), x);
        this.operation = then.getOperation();
        this.y = VariableString.toString(then.getY(), y);
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.destinationVariableName = VariableString.toString(then.getDestinationVariableName(), destinationVariableName);
        this.itemPlacement = then.getItemPlacement();
        this.delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        this.index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    public void setX(String x) {
        this.x = x;
        propertyChanged("x", x);
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
        propertyChanged("operation", operation);
    }

    public void setY(String y) {
        this.y = y;
        propertyChanged("y", y);
    }

    public void setDestinationVariableSource(VariableSource destinationVariableSource) {
        this.destinationVariableSource = destinationVariableSource;
        propertyChanged("destinationVariableSource", destinationVariableSource);
    }

    public void setDestinationVariableName(String destinationVariableName) {
        this.destinationVariableName = destinationVariableName;
        propertyChanged("destinationVariableName", destinationVariableName);
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
        if (StringUtils.isEmpty(x)) {
            errors.add("X is required");
        }
        if (operation.getInputs() > 1 && StringUtils.isEmpty(y)) {
            errors.add("Y is required");
        }
        if (StringUtils.isEmpty(destinationVariableName)) {
            errors.add("Destination Variable Name is required");
        } else if (!VariableString.isValidVariableName(destinationVariableName)) {
            errors.add("Destination Variable Name is invalid");
        }
        if (destinationVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
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
        ruleOperation.setX(VariableString.getAsVariableString(x));
        ruleOperation.setOperation(operation);
        ruleOperation.setY(VariableString.getAsVariableString(y));
        ruleOperation.setDestinationVariableSource(destinationVariableSource);
        ruleOperation.setDestinationVariableName(VariableString.getAsVariableString(destinationVariableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return operation.getName();
    }

    @Override
    public RuleOperationModelType<ThenEvaluateModel, ThenEvaluate> getType() {
        return ThenModelType.Evaluate;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(destinationVariableName) ?
                List.of(new VariableSourceEntry(destinationVariableSource, destinationVariableName)) :
                Collections.emptyList();
    }
}
