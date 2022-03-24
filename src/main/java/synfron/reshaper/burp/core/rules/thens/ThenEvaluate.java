package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;

public class ThenEvaluate extends Then<ThenEvaluate> {
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
    public RuleResponse perform(IEventInfo eventInfo) {
        boolean hasError = true;
        String result = null;
        try {
            Variables variables = switch (destinationVariableSource) {
                case Event -> eventInfo.getVariables();
                case Global -> GlobalVariables.get();
                default -> null;
            };
            if (variables != null) {
                Variable variable = variables.add(destinationVariableName.getText(eventInfo));
                result = evaluate(eventInfo);
                if (result != null) {
                    variable.setValue(result);
                }
            }
            hasError = result == null;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("X", VariableString.getTextOrDefault(eventInfo, x, null)),
                    Pair.of("Y", VariableString.getTextOrDefault(eventInfo, y, null)),
                    Pair.of("destinationVariableSource", destinationVariableSource),
                    Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null)),
                    Pair.of("result", result)
            ));
        }
        return RuleResponse.Continue;
    }

    private String evaluate(IEventInfo eventInfo) {
        Double value1 = operation.getInputs() > 0 ? VariableString.getDoubleOrDefault(eventInfo, x, null) : null;
        Double value2 = operation.getInputs() > 1 ? VariableString.getDoubleOrDefault(eventInfo, y, null) : null;
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
