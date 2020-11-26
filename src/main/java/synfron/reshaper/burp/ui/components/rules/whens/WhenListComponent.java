package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModelType;

import java.util.Collections;
import java.util.List;

public class WhenListComponent extends RuleOperationListComponent<WhenModel<?,?>> {

    public WhenListComponent(RuleModel model) {
        super(model);
    }

    @Override
    protected List<WhenModel<?, ?>> getRuleOperations() {
        return model.getWhens();
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return Collections.unmodifiableList(WhenModelType.getTypes());
    }

    @Override
    protected WhenModel<?, ?> getModel(RuleOperationModelType<?,?> ruleOperationModelType) {
        return WhenModel.getNewModel(ruleOperationModelType);
    }
}
