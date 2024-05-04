package synfron.reshaper.burp.ui.components.rules.thens;

import burp.BurpExtender;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModel;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModelType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModelType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ThenListComponent extends RuleOperationListComponent<ThenModel<?,?>> {

    private final IEventListener<PropertyChangedArgs> generalSettingsChangedListener = this::onGeneralSettingsChanged;

    public ThenListComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
        BurpExtender.getGeneralSettings().withListener(generalSettingsChangedListener);
    }

    @Override
    protected List<ThenModel<?, ?>> getRuleOperations() {
        return model.getThens();
    }

    private void onGeneralSettingsChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("hiddenThenTypes")) {
            syncOperationSelectorList();
        }
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return ThenModelType.getTypes(protocolType).stream()
                .filter(type -> !BurpExtender.getGeneralSettings().getHiddenThenTypes().contains(type.getName()))
                .collect(Collectors.toList());
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
