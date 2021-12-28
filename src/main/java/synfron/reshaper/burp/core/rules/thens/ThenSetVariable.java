package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.Collections;

public class ThenSetVariable extends ThenSet<ThenSetVariable> {

    @Getter @Setter
    private VariableSource targetSource = VariableSource.Global;
    @Getter @Setter
    private VariableString variableName;

    public RuleResponse perform(IEventInfo eventInfo)
    {
        try {
            String replacementText = getReplacementValue(eventInfo);
            setValue(eventInfo, replacementText);
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
        }
        return RuleResponse.Continue;
    }

    private void setValue(IEventInfo eventInfo, String replacementText)
    {
        Variables variables = switch (targetSource) {
            case Event -> eventInfo.getVariables();
            case Global -> GlobalVariables.get();
            default -> null;
        };
        if (variables != null)
        {
            Variable variable = variables.add(variableName.getText(eventInfo));
            if (destinationMessageValuePath != null && destinationMessageValueType != MessageValueType.Text && variable.getValue() != null)
            {
                switch (destinationMessageValueType) {
                    case Json -> replacementText = TextUtils.setJsonValue(variable.getValue().toString(), destinationMessageValuePath.getText(eventInfo), replacementText);
                    case Html -> replacementText = TextUtils.setHtmlValue(variable.getValue().toString(), destinationMessageValuePath.getText(eventInfo), replacementText);
                    case Params -> replacementText = TextUtils.setParamValue(variable.getValue().toString(), destinationMessageValuePath.getText(eventInfo), replacementText);
                }
            }
            variable.setValue(replacementText);
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Arrays.asList(
                    Pair.of("sourceMessageValue", isUseMessageValue() ? getSourceMessageValue() : null),
                    Pair.of("sourceIdentifier", isUseMessageValue() && getSourceMessageValue().isIdentifierRequired() ? VariableString.getTextOrDefault(eventInfo, getSourceIdentifier(), null) : null),
                    Pair.of("sourceIdentifierPlacement", isUseMessageValue() ? getSourceIdentifierPlacement() : null),
                    Pair.of("sourceValueType", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? getSourceMessageValueType() : null),
                    Pair.of("sourceValuePath", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, getSourceMessageValuePath(), null) : null),
                    Pair.of("sourceText", !isUseMessageValue() ? VariableString.getTextOrDefault(eventInfo, getText(), null) : null),
                    Pair.of("regexPattern", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getRegexPattern(), null) : null),
                    Pair.of("replacementText", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getReplacementText(), null) : null),
                    Pair.of("destinationVariableSource", targetSource),
                    Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, variableName, null)),
                    Pair.of("destinationValueType", destinationMessageValueType != MessageValueType.Text ? destinationMessageValueType : null),
                    Pair.of("destinationValuePath", destinationMessageValueType != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, destinationMessageValuePath, null) : null),
                    Pair.of("input", replacementText)
            ));
        }
    }

    @Override
    public RuleOperationType<ThenSetVariable> getType() {
        return ThenType.SetVariable;
    }
}
