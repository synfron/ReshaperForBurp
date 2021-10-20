package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;
import java.util.Collections;

public class ThenSetValue extends ThenSet<ThenSetValue> {
    @Getter @Setter
    private MessageValue destinationMessageValue = MessageValue.HttpRequestBody;
    @Getter @Setter
    private VariableString destinationIdentifier;

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
        }
        return RuleResponse.Continue;
    }

    private void setValue(EventInfo eventInfo, String replacementText)
    {
        if (destinationMessageValueType != MessageValueType.Text) {
            String fullText = MessageValueHandler.getValue(eventInfo, destinationMessageValue, destinationIdentifier);
            switch (destinationMessageValueType) {
                case Json -> replacementText = TextUtils.setJsonValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
                case Html -> replacementText = TextUtils.setHtmlValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
            }
        }
        MessageValueHandler.setValue(eventInfo, destinationMessageValue, destinationIdentifier, replacementText);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Arrays.asList(
                Pair.of("sourceMessageValue", isUseMessageValue() ? getSourceMessageValue() : null),
                Pair.of("sourceIdentifier", isUseMessageValue() && MessageValueHandler.hasIdentifier(getSourceMessageValue()) ? VariableString.getTextOrDefault(eventInfo, getSourceIdentifier(), null) : null),
                Pair.of("sourceValueType", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? getSourceMessageValueType() : null),
                Pair.of("sourceValuePath", isUseMessageValue() && getSourceMessageValueType() != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, getSourceMessageValuePath(), null) : null),
                Pair.of("sourceText", !isUseMessageValue() ? VariableString.getTextOrDefault(eventInfo, getText(), null) : null),
                Pair.of("regexPattern", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getRegexPattern(), null) : null),
                Pair.of("replacementText", isUseReplace() ? VariableString.getTextOrDefault(eventInfo, getReplacementText(), null) : null),
                Pair.of("destinationMessageValue", destinationMessageValue),
                Pair.of("destinationIdentifier",  MessageValueHandler.hasIdentifier(getDestinationMessageValue()) ? VariableString.getTextOrDefault(eventInfo, destinationIdentifier, null) : null),
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
