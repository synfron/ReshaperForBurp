package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenBreak extends Then<ThenBreak> {
    @Getter
    @Setter
    private RuleResponse breakType = RuleResponse.BreakThens;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, breakType);
        return breakType;
    }

    @Override
    public RuleOperationType<ThenBreak> getType() {
        return ThenType.Break;
    }
}
