package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.rules.*;

public class WhenEventDirection extends When<WhenEventDirection> implements IHttpRuleOperation {
    @Getter @Setter
    private HttpDataDirection dataDirection = HttpDataDirection.Request;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = ((HttpEventInfo)eventInfo).getDataDirection() == dataDirection;
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Equals, dataDirection, ((HttpEventInfo)eventInfo).getDataDirection(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenEventDirection> getType() {
        return WhenType.EventDirection;
    }
}
