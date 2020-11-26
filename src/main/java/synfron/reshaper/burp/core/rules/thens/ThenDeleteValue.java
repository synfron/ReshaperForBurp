package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenDeleteValue extends Then<ThenDeleteValue> {
    private final transient MessageValueHandler messageValueHandler = new MessageValueHandler();
    @Getter
    @Setter
    private MessageValue messageValue = MessageValue.HttpRequestBody;
    @Getter
    @Setter
    private VariableString identifier;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        messageValueHandler.setValue(eventInfo, messageValue, identifier, null);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDeleteValue> getType() {
        return ThenType.DeleteValue;
    }
}
