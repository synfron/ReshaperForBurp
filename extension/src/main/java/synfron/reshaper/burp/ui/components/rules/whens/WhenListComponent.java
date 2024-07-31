package synfron.reshaper.burp.ui.components.rules.whens;

import burp.BurpExtender;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.ui.components.rules.RuleOperationListComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModelType;

import java.util.List;
import java.util.stream.Collectors;

public class WhenListComponent extends RuleOperationListComponent<WhenModel<?,?>> {

    private final IEventListener<PropertyChangedArgs> generalSettingsChangedListener = this::onGeneralSettingsChanged;
    private final IEventListener<PropertyChangedArgs> modelPropertyChangedListener = this::onModelPropertyChanged;

    public WhenListComponent(ProtocolType protocolType, RuleModel model) {
        super(protocolType, model);
        BurpExtender.getGeneralSettings().withListener(generalSettingsChangedListener);

        model.getPropertyChangedEvent().add(modelPropertyChangedListener);
    }

    private void onModelPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("whens")) {
            refreshOperationsList();
        }
    }

    @Override
    protected List<WhenModel<?, ?>> getRuleOperations() {
        return model.getWhens();
    }

    private void onGeneralSettingsChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("hiddenWhenTypes")) {
            syncOperationSelectorList();
        }
    }

    @Override
    protected List<RuleOperationModelType<?,?>> getRuleOperationModelTypes() {
        return WhenModelType.getTypes(protocolType).stream()
                .filter(type -> !BurpExtender.getGeneralSettings().getHiddenWhenTypes().contains(type.getName()))
                .collect(Collectors.toList());
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
