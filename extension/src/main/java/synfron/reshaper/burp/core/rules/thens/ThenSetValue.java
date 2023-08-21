package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.IItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;
import java.util.Collections;

public class ThenSetValue extends ThenSet<ThenSetValue> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private MessageValue destinationMessageValue;
    @Getter @Setter
    private VariableString destinationIdentifier;
    @Getter @Setter
    private SetItemPlacement destinationIdentifierPlacement = SetItemPlacement.Only;

    public ThenSetValue() {
        setUseMessageValue(false);
    }

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
        if (destinationMessageValueType != MessageValueType.Text) {
            String fullText = MessageValueHandler.getValue(
                    eventInfo,
                    destinationMessageValue,
                    destinationIdentifier,
                    IItemPlacement.toGet(destinationIdentifierPlacement)
            );
            switch (destinationMessageValueType) {
                case Json -> replacementText = TextUtils.setJsonValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
                case Html -> replacementText = TextUtils.setHtmlValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
                case Params -> replacementText = TextUtils.setParamValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
            }
        }
        MessageValueHandler.setValue(eventInfo, destinationMessageValue, destinationIdentifier, destinationIdentifierPlacement, replacementText);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Arrays.asList(
                Pair.of("sourceMessageValue", isUseMessageValue() ? getSourceMessageValue() : null),
                Pair.of("sourceIdentifier", isUseMessageValue() && getSourceMessageValue().isIdentifierRequired() ? VariableString.getTextOrDefault(eventInfo, getSourceIdentifier(), null) : null),
                Pair.of("sourceIdentifierPlacement", isUseMessageValue() ? getSourceIdentifierPlacement() : null),
                Pair.of("sourceValueType", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? getSourceMessageValueType() : null),
                Pair.of("sourceValuePath", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, getSourceMessageValuePath(), null) : null),
                Pair.of("sourceText", !isUseMessageValue() ? VariableString.getTextOrDefault(eventInfo, getText(), null) : null),
                Pair.of("regexPattern", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getRegexPattern(), null) : null),
                Pair.of("replacementText", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getReplacementText(), null) : null),
                Pair.of("destinationMessageValue", destinationMessageValue),
                Pair.of("destinationIdentifier",  getDestinationMessageValue().isIdentifierRequired() ? VariableString.getTextOrDefault(eventInfo, destinationIdentifier, null) : null),
                Pair.of("destinationIdentifierPlacement", getDestinationMessageValue().isIdentifierRequired() ? destinationIdentifierPlacement : null),
                Pair.of("destinationValueType", destinationMessageValueType != MessageValueType.Text ? destinationMessageValueType : null),
                Pair.of("destinationValuePath", destinationMessageValueType != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, destinationMessageValuePath, null) : null),
                Pair.of("input", replacementText)
        ));
    }

    @Override
    public RuleOperationType<ThenSetValue> getType() {
        return ThenType.SetValue;
    }
}
