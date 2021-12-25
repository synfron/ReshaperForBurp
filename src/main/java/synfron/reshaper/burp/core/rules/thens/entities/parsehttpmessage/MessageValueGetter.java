package synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage;

import lombok.Data;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;

@Data
public class MessageValueGetter implements Serializable {
    private MessageValue sourceMessageValue;
    private VariableString sourceIdentifier;
    private VariableSource destinationVariableSource = VariableSource.Global;
    private VariableString destinationVariableName;

    public MessageValueGetter() {}

    public MessageValueGetter(DataDirection dataDirection) {
        sourceMessageValue = dataDirection == DataDirection.Request ? MessageValue.HttpRequestBody : MessageValue.HttpResponseBody;
    }
}
