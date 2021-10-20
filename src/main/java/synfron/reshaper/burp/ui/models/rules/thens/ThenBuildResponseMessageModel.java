package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenBuildResponseMessage;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenBuildResponseMessageModel extends ThenBuildMessageModel<ThenBuildResponseMessageModel, ThenBuildResponseMessage> {
    public ThenBuildResponseMessageModel(ThenBuildResponseMessage then, Boolean isNew) {
        super(then, isNew);
    }

    @Override
    public RuleOperationModelType<ThenBuildResponseMessageModel, ThenBuildResponseMessage> getType() {
        return ThenModelType.BuildResponseMessage;
    }
}
