package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModel;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModelType;

import java.util.Collections;
import java.util.List;

public class ThenListComponent extends RuleOperationListComponent<ThenModel<?,?>> {

    public ThenListComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
    }

    @Override
    protected List<ThenModel<?, ?>> getRuleOperations() {
        return model.getThens();
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return Collections.unmodifiableList(ThenModelType.getTypes(protocolType));
    }

    @Override
    protected ThenModel<?, ?> getNewModel(RuleOperationModelType<?,?> ruleOperationModelType) {
        return ThenModel.getNewModel(protocolType, ruleOperationModelType);
    }

    @Override
    protected <R extends IRuleOperation<?>> ThenModel<?, ?> getModel(R then) {
        return ThenModel.getModel(protocolType, (Then<?>)then);
    }
}
