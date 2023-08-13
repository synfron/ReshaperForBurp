package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.Collections;

public class ThenSetVariable extends ThenSet<ThenSetVariable> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter @Setter
    private VariableSource targetSource = VariableSource.Global;
    @Getter @Setter
    private VariableString variableName;
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;



    public RuleResponse perform(EventInfo eventInfo)
    {
        try {
            String replacementText = getReplacementValue(eventInfo);
            setValue(eventInfo, replacementText);
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
            throw e;
        }
        return RuleResponse.Continue;
    }

    private void setValue(EventInfo eventInfo, String replacementText)
    {
        Variables variables = getVariables(targetSource, eventInfo);
        if (variables != null)
        {
            Variable variable = variables.add(Variables.asKey(variableName.getText(eventInfo), targetSource.isList()));
            if (destinationMessageValuePath != null && destinationMessageValueType != MessageValueType.Text && variable.hasValue())
            {
                String value = StringUtils.defaultString(TextUtils.toString(variable.getValue(IListItemPlacement.toGet(itemPlacement), index.getInt(eventInfo))));
                switch (destinationMessageValueType) {
                    case Json -> replacementText = TextUtils.setJsonValue(value, destinationMessageValuePath.getText(eventInfo), replacementText);
                    case Html -> replacementText = TextUtils.setHtmlValue(value, destinationMessageValuePath.getText(eventInfo), replacementText);
                    case Params -> replacementText = TextUtils.setParamValue(value, destinationMessageValuePath.getText(eventInfo), replacementText);
                }
            }
            variable.setValue(itemPlacement, VariableString.getTextOrDefault(eventInfo, delimiter, "\n"), VariableString.getIntOrDefault(eventInfo, index, 0), replacementText);
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
                    Pair.of("itemPlacement", targetSource.isList() ? itemPlacement : null),
                    Pair.of("delimiter", targetSource.isList() && itemPlacement.isHasDelimiterSetter() ? VariableString.getTextOrDefault(eventInfo, delimiter, null) : null),
                    Pair.of("index", targetSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null),
                    Pair.of("input", replacementText)
            ));
        }
    }

    @Override
    public RuleOperationType<ThenSetVariable> getType() {
        return ThenType.SetVariable;
    }
}
