package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenSetEventDirection extends Then<ThenSetEventDirection> implements IHttpRuleOperation {
    @Getter
    @Setter
    private HttpDataDirection dataDirection = HttpDataDirection.Request;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        ((HttpEventInfo)eventInfo).setDataDirection(dataDirection);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, dataDirection);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenSetEventDirection> getType() {
        return ThenType.SetEventDirection;
    }
}
