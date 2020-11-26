package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenEventDirection extends When<WhenEventDirection> {
    @Getter @Setter
    private DataDirection dataDirection = DataDirection.Request;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        return eventInfo.getDataDirection() == dataDirection;
    }

    @Override
    public RuleOperationType<WhenEventDirection> getType() {
        return WhenType.EventDirection;
    }
}
