package synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage;

import lombok.Data;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;

@Data
public class MessageValueSetter implements Serializable {
    private MessageValue destinationMessageValue;
    private VariableString destinationIdentifier;
    private SetItemPlacement destinationIdentifierPlacement = SetItemPlacement.Only;
    private VariableString sourceText;

    public MessageValueSetter() {}

    public MessageValueSetter(DataDirection dataDirection) {
        destinationMessageValue = dataDirection == DataDirection.Request ? MessageValue.HttpRequestBody : MessageValue.HttpResponseBody;
    }
}
