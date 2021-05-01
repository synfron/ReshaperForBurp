package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public abstract class WhenModel<P extends WhenModel<P, T>, T extends When<T>> extends RuleOperationModel<P, T>  {

    @Getter
    private boolean negate;
    @Getter
    private boolean useOrCondition;

    public WhenModel(T ruleOperation, Boolean isNew) {
        super(ruleOperation, isNew);
        negate = ruleOperation.isNegate();
        useOrCondition = ruleOperation.isUseOrCondition();
    }

    public WhenModel<?,?> withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
        propertyChanged("negate", negate);
    }

    public void setUseOrCondition(boolean useOrCondition) {
        this.useOrCondition = useOrCondition;
        propertyChanged("useOrCondition", useOrCondition);
    }

    @Override
    public boolean persist() {
        ruleOperation.setNegate(negate);
        ruleOperation.setUseOrCondition(useOrCondition);

        setValidated(true);
        return true;
    }

    public static WhenModel<?, ?> getNewModel(RuleOperationModelType<?, ?> ruleOperationModelType) {
        return (WhenModel<?, ?>) ObjectUtils.construct(
                ruleOperationModelType.getType(),
                ObjectUtils.construct(
                        ruleOperationModelType.getRuleOperationType().getType()
                ),
                true
        );
    }

    public static WhenModel<?, ?> getModel(When<?> when) {
        return (WhenModel<?, ?>) ObjectUtils.construct(
                WhenModelType.getTypes().stream()
                        .filter(type -> type.getRuleOperationType() == when.getType())
                        .findFirst()
                        .get().getType(),
                when,
                false
        );
    }


}
