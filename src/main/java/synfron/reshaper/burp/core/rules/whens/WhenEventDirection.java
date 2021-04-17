package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenEventDirection extends When<WhenEventDirection> {
    @Getter @Setter
    private DataDirection dataDirection = DataDirection.Request;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = (eventInfo.getDataDirection() == dataDirection) == !isNegate();
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, MatchType.Equals, dataDirection, null, eventInfo.getDataDirection(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenEventDirection> getType() {
        return WhenType.EventDirection;
    }
}
