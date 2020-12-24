package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

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
        String replacementText = getReplacementValue(eventInfo);
        setValue(eventInfo, replacementText);
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
    }

    @Override
    public RuleOperationType<ThenSetValue> getType() {
        return ThenType.SetValue;
    }
}
