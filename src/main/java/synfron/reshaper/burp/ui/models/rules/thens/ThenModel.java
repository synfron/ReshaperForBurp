package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public abstract class ThenModel<P extends ThenModel<P, T>, T extends Then<T>> extends RuleOperationModel<P, T> {

    public ThenModel(T ruleOperation, Boolean isNew) {
        super(ruleOperation, isNew);
    }

    public ThenModel<?,?> withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public static ThenModel<?,?> getNewModel(RuleOperationModelType<?,?> ruleOperationProxyType) {
        return (ThenModel<?, ?>) ObjectUtils.construct(
                ruleOperationProxyType.getType(),
                ObjectUtils.construct(
                        ruleOperationProxyType.getRuleOperationType().getType()
                ),
                true
        );
    }

    public static ThenModel<?,?> getModel(Then<?> then) {
        return (ThenModel<?, ?>) ObjectUtils.construct(
                ThenModelType.getTypes().stream()
                        .filter(type -> type.getRuleOperationType() == then.getType())
                        .findFirst()
                        .get().getType(),
                then,
                false
        );
    }
}
