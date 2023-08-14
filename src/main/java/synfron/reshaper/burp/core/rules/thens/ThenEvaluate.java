package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

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
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        String result = null;
        Object value1 = null;
        Object value2 = null;
        try {
            Variables variables = getVariables(destinationVariableSource, eventInfo);
            if (variables != null) {
                Variable variable = variables.add(Variables.asKey(destinationVariableName.getText(eventInfo), destinationVariableSource.isList()));
                if (operation.isNumeric()) {
                    value1 = operation.getInputs() > 0 ? VariableString.getDoubleOrDefault(eventInfo, x, null) : null;
                    value2 = operation.getInputs() > 1 ? VariableString.getDoubleOrDefault(eventInfo, y, null) : null;
                    result = evaluate((Double)value1, (Double)value2);
                }
                else {
                    value1 = operation.getInputs() > 0 ? VariableString.getTextOrDefault(eventInfo, x, "") : null;
                    value2 = operation.getInputs() > 1 ? VariableString.getTextOrDefault(eventInfo, y, "") : null;
                    result = evaluate((String)value1, (String)value2);
                }
                if (result != null) {
                    variable.setValue(itemPlacement, VariableString.getTextOrDefault(eventInfo, delimiter, "\n"), VariableString.getIntOrDefault(eventInfo, index, 0), result);
                }
            }
            hasError = result == null;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("X", TextUtils.toString(value1)),
                    Pair.of("Y", TextUtils.toString(value2)),
                    Pair.of("destinationVariableSource", destinationVariableSource),
                    Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null)),
                    Pair.of("itemPlacement", destinationVariableSource.isList() ? itemPlacement : null),
                    Pair.of("delimiter", destinationVariableSource.isList() && itemPlacement.isHasDelimiterSetter() ? delimiter : null),
                    Pair.of("index", destinationVariableSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null),
                    Pair.of("result", result)
            ));
        }
        return RuleResponse.Continue;
    }

    private String evaluate(String value1, String value2) {
        String result = null;
        if (operation.getMinInputs() == 0
                || (operation.getMinInputs() == 1 && value1 != null)
                || (operation.getMinInputs() == 2 && value1 != null && value2 != null)) {
            result = switch (operation) {
                case Equals -> toString(StringUtils.equals(value1, value2));
                case NotEquals -> toString(!StringUtils.equals(value1, value2));
                case Contains -> toString(StringUtils.contains(value1, value2));
                case Not -> toString(BooleanUtils.toBoolean(value1));
                default -> null;
            };
        }
        return result;
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
                case GreaterThan -> toString(value1 > value2);
                case GreaterThanOrEquals -> toString(value1 >= value2);
                case LessThan -> toString(value1 < value2);
                case LessThanOrEquals -> toString(value1 <= value2);
                case Abs -> toString(Math.abs(value1));
                case Mod -> toString(value1 % value2);
                case Round -> toString(Math.round(value1));
                default -> null;
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
