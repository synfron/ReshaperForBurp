package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.components.rules.RuleOperationsComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;

public class WhensComponent extends RuleOperationsComponent<WhenModel<?,?>> {

    public WhensComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
    }

    @Override
    protected RuleOperationListComponent<WhenModel<?,?>> getOperationList() {
        WhenListComponent component = new WhenListComponent(protocolType, model);
        component.setSelectionContainer(new WhenContainerComponent(protocolType));
        return component;
    }

    @Override
    protected String getHeaderText() {
        return "Whens";
    }
}
