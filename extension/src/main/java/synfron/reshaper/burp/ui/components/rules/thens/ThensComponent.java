package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.components.rules.RuleOperationsComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModel;

public class ThensComponent extends RuleOperationsComponent<ThenModel<?,?>> {

    public ThensComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
    }

    @Override
    protected RuleOperationListComponent<ThenModel<?,?>> getOperationList() {
        ThenListComponent component = new ThenListComponent(protocolType, model);
        component.setSelectionContainer(new ThenContainerComponent(protocolType));
        return component;
    }

    @Override
    protected String getHeaderText() {
        return "Thens";
    }
}
