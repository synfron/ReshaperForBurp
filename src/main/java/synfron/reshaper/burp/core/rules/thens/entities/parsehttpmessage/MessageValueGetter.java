package synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage;

import lombok.Data;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;

@Data
public class MessageValueGetter implements Serializable {
    private MessageValue sourceMessageValue;
    private VariableString sourceIdentifier;
    private GetItemPlacement sourceIdentifierPlacement = GetItemPlacement.Last;
    private VariableSource destinationVariableSource = VariableSource.Global;
    private VariableString destinationVariableName;

    public MessageValueGetter() {}

    public MessageValueGetter(HttpDataDirection dataDirection) {
        sourceMessageValue = dataDirection == HttpDataDirection.Request ? MessageValue.HttpRequestBody : MessageValue.HttpResponseBody;
    }
}
