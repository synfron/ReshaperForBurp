package synfron.reshaper.burp.ui.components.rules.thens;

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

    public ThenListComponent(RuleModel model) {
        super(model);
    }

    @Override
    protected List<ThenModel<?, ?>> getRuleOperations() {
        return model.getThens();
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return Collections.unmodifiableList(ThenModelType.getTypes());
    }

    @Override
    protected ThenModel<?, ?> getNewModel(RuleOperationModelType<?,?> ruleOperationModelType) {
        return ThenModel.getNewModel(ruleOperationModelType);
    }

    @Override
    protected <R extends IRuleOperation<?>> ThenModel<?, ?> getModel(R then) {
        return ThenModel.getModel((Then<?>)then);
    }
}
