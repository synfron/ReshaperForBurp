package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenEventDirection extends When<WhenEventDirection> {
    @Getter @Setter
    private DataDirection dataDirection = DataDirection.Request;

    @Override
    public boolean isMatch(IEventInfo eventInfo) {
        boolean isMatch = eventInfo.getDataDirection() == dataDirection;
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Equals, dataDirection, eventInfo.getDataDirection(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenEventDirection> getType() {
        return WhenType.EventDirection;
    }
}
