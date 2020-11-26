package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.components.rules.RuleOperationsComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;

public class WhensComponent extends RuleOperationsComponent<WhenModel<?,?>> {
    public WhensComponent(RuleModel model) {
        super(model);
    }

    @Override
    protected RuleOperationListComponent<WhenModel<?,?>> getOperationList() {
        WhenListComponent component = new WhenListComponent(model);
        component.setSelectionContainer(new WhenContainerComponent());
        return component;
    }

    @Override
    protected String getHeaderText() {
        return "Whens";
    }
}
