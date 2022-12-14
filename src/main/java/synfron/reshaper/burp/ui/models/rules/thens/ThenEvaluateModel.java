package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenEvaluate;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
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

    public ThenEvaluateModel(ProtocolType protocolType, ThenEvaluate then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.x = VariableString.toString(then.getX(), x);
        this.operation = then.getOperation();
        this.y = VariableString.toString(then.getY(), y);
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.destinationVariableName = VariableString.toString(then.getDestinationVariableName(), destinationVariableName);
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
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setX(VariableString.getAsVariableString(x));
        ruleOperation.setOperation(operation);
        ruleOperation.setY(VariableString.getAsVariableString(y));
        ruleOperation.setDestinationVariableSource(destinationVariableSource);
        ruleOperation.setDestinationVariableName(VariableString.getAsVariableString(destinationVariableName));
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
