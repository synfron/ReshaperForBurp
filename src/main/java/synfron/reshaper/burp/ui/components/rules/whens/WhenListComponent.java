package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModelType;

import java.util.Collections;
import java.util.List;

public class WhenListComponent extends RuleOperationListComponent<WhenModel<?,?>> {

    public WhenListComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
    }

    @Override
    protected List<WhenModel<?, ?>> getRuleOperations() {
        return model.getWhens();
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return Collections.unmodifiableList(WhenModelType.getTypes(protocolType));
    }

    @Override
    protected WhenModel<?, ?> getNewModel(RuleOperationModelType<?,?> ruleOperationModelType) {
        return WhenModel.getNewModel(protocolType, ruleOperationModelType);
    }

    @Override
    protected <R extends IRuleOperation<?>> WhenModel<?, ?> getModel(R when) {
        return WhenModel.getModel(protocolType, (When<?>)when);
    }
}
