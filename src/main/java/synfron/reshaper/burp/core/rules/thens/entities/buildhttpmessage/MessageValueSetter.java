package synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage;

import lombok.Data;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.vars.VariableString;

@Data
public class MessageValueSetter {
    private MessageValue destinationMessageValue;
    private VariableString destinationIdentifier;
    private VariableString sourceText;

    public MessageValueSetter() {}

    public MessageValueSetter(DataDirection dataDirection) {
        destinationMessageValue = dataDirection == DataDirection.Request ? MessageValue.HttpRequestBody : MessageValue.HttpResponseBody;
    }
}
