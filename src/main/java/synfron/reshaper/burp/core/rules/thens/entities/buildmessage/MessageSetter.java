package synfron.reshaper.burp.core.rules.thens.entities.buildmessage;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.vars.VariableString;

public class MessageSetter {
    @Getter @Setter
    private MessageValue messageValue = MessageValue.HttpRequestBody;
    @Getter @Setter
    private VariableString identifier;
    @Getter @Setter
    private VariableString text;
}
