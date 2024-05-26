package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.InterceptResponse;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenIntercept;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenInterceptModel extends ThenModel<ThenInterceptModel, ThenIntercept> {

    @Getter
    private InterceptResponse interceptResponse;

    public ThenInterceptModel(ProtocolType protocolType, ThenIntercept then, Boolean isNew) {
        super(protocolType, then, isNew);
        interceptResponse = then.getInterceptResponse();
    }

    public void setInterceptResponse(InterceptResponse interceptResponse) {
        this.interceptResponse = interceptResponse;
        propertyChanged("interceptResponse", interceptResponse);
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setInterceptResponse(interceptResponse);
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return interceptResponse.getName();
    }

    @Override
    public RuleOperationModelType<ThenInterceptModel, ThenIntercept> getType() {
        return ThenModelType.Intercept;
    }
}
