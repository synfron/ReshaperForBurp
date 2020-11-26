package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenSetEventDirection extends Then<ThenSetEventDirection> {
    @Getter
    @Setter
    private DataDirection dataDirection = DataDirection.Request;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        eventInfo.setDataDirection(dataDirection);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenSetEventDirection> getType() {
        return ThenType.SetEventDirection;
    }
}
