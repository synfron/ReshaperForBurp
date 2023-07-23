package synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
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
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    public MessageValueGetter() {}

    public MessageValueGetter(HttpDataDirection dataDirection) {
        sourceMessageValue = dataDirection == HttpDataDirection.Request ? MessageValue.HttpRequestBody : MessageValue.HttpResponseBody;
    }
}
