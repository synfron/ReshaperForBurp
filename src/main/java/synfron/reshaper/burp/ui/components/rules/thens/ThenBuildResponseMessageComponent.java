package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildResponseMessage;
import synfron.reshaper.burp.ui.models.rules.thens.ThenBuildResponseMessageModel;


public class ThenBuildResponseMessageComponent extends ThenBuildMessageComponent<ThenBuildResponseMessageModel, ThenBuildResponseMessage> {
    public ThenBuildResponseMessageComponent(ThenBuildResponseMessageModel then) {
        super(then, DataDirection.Response);
    }
}
