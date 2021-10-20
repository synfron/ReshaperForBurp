package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildRequestMessage;
import synfron.reshaper.burp.ui.models.rules.thens.ThenBuildRequestMessageModel;

public class ThenBuildRequestMessageComponent extends ThenBuildMessageComponent<ThenBuildRequestMessageModel, ThenBuildRequestMessage> {
    public ThenBuildRequestMessageComponent(ThenBuildRequestMessageModel then) {
        super(then, DataDirection.Request);
    }
}
