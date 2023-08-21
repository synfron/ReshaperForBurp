package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.InterceptResponse;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenIntercept extends Then<ThenIntercept> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private InterceptResponse interceptResponse = InterceptResponse.UserDefined;

    public RuleResponse perform(EventInfo eventInfo) {
        eventInfo.setDefaultInterceptResponse(interceptResponse);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, interceptResponse);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenIntercept> getType() {
        return ThenType.Intercept;
    }

    public static InterceptResponse[] getSupportedResponses() {
        return new InterceptResponse[] { InterceptResponse.UserDefined, InterceptResponse.Disable, InterceptResponse.Intercept };
    }
}
