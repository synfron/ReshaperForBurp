package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.Arrays;

public class ThenEvaluate extends Then<ThenEvaluate> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter
    @Setter
    private VariableString x;
    @Getter
    @Setter
    private Operation operation = Operation.Add;
    @Getter
    @Setter
    private VariableString y;
    @Getter @Setter
    private VariableSource destinationVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString destinationVariableName;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        String result = null;
        Double value1 = null;
        Double value2 = null;
        try {
            Variables variables = getVariables(destinationVariableSource, eventInfo);
            if (variables != null) {
                Variable variable = variables.add(destinationVariableName.getText(eventInfo));
                value1 = operation.getInputs() > 0 ? VariableString.getDoubleOrDefault(eventInfo, x, null) : null;
                value2 = operation.getInputs() > 1 ? VariableString.getDoubleOrDefault(eventInfo, y, null) : null;
                result = evaluate(value1, value2);
                if (result != null) {
                    variable.setValue(result);
                }
            }
            hasError = result == null;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("X", value1),
                    Pair.of("Y", value2),
                    Pair.of("destinationVariableSource", destinationVariableSource),
                    Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null)),
                    Pair.of("result", result)
            ));
        }
        return RuleResponse.Continue;
    }

    private String evaluate(Double value1, Double value2) {
        String result = null;
        if ((operation.getInputs() == 1 && value1 != null)
                || (operation.getInputs() == 2 && value1 != null && value2 != null)) {
            result = switch (operation) {
                case Add -> toString(value1 + value2);
                case Subtract -> toString(value1 - value2);
                case Multiply -> toString(value1 * value2);
                case DivideBy -> toString(value1 / value2);
                case Increment -> toString(value1 + 1);
                case Decrement -> toString(value1 - 1);
                case Equals -> toString(value1.equals(value2));
                case GreaterThan -> toString(value1 > value2);
                case GreaterThanOrEquals -> toString(value1 >= value2);
                case LessThan -> toString(value1 < value2);
                case LessThanOrEquals -> toString(value1 <= value2);
                case Abs -> toString(Math.abs(value1));
                case Mod -> toString(value1 % value2);
                case Round -> toString(Math.round(value1));
            };
        }
        return result;
    }

    private String toString(double value) {
        return StringUtils.removeEnd(String.valueOf(value), ".0");
    }

    private String toString(boolean value) {
        return String.valueOf(value);
    }

    @Override
    public RuleOperationType<ThenEvaluate> getType() {
        return ThenType.Evaluate;
    }
}
