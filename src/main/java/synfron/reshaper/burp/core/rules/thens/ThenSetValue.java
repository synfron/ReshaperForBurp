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
            switch (destinationMessageValueType)
            {
                case Json:
                    replacementText = TextUtils.setJsonValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
                    break;
                case Html:
                    replacementText = TextUtils.setHtmlValue(fullText, destinationMessageValuePath.getText(eventInfo), replacementText);
                    break;
            }
        }
        MessageValueHandler.setValue(eventInfo, destinationMessageValue, destinationIdentifier, replacementText);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Arrays.asList(
                Pair.of("destinationMessageValue", destinationMessageValue),
                Pair.of("destinationIdentifier", VariableString.getTextOrDefault(eventInfo, destinationIdentifier, null)),
                Pair.of("valueType", destinationMessageValueType != MessageValueType.Text ? destinationMessageValueType : null),
                Pair.of("valuePath", destinationMessageValueType != MessageValueType.Text ? VariableString.getTextOrDefault(eventInfo, destinationMessageValuePath, null) : null),
                Pair.of("input", replacementText)
        ));
    }

    @Override
    public RuleOperationType<ThenSetValue> getType() {
        return ThenType.SetValue;
    }
}
