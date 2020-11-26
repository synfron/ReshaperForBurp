package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
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
    protected ThenModel<?, ?> getModel(RuleOperationModelType<?,?> ruleOperationModelType) {
        return ThenModel.getNewModel(ruleOperationModelType);
    }
}
