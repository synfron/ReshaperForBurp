package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenSetEventDirection extends Then<ThenSetEventDirection> {
    @Getter
    @Setter
    private DataDirection dataDirection = DataDirection.Request;

    @Override
    public RuleResponse perform(IEventInfo eventInfo) {
        eventInfo.setDataDirection(dataDirection);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, dataDirection);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenSetEventDirection> getType() {
        return ThenType.SetEventDirection;
    }
}
