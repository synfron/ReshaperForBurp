package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenBuildRequestMessage;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenBuildRequestMessageModel extends ThenBuildMessageModel<ThenBuildRequestMessageModel, ThenBuildRequestMessage> {
    public ThenBuildRequestMessageModel(ThenBuildRequestMessage then, Boolean isNew) {
        super(then, isNew);
    }

    @Override
    public RuleOperationModelType<ThenBuildRequestMessageModel, ThenBuildRequestMessage> getType() {
        return ThenModelType.BuildRequestMessage;
    }
}
