package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenWebSocketEventDirection extends When<WhenWebSocketEventDirection> implements IWebSocketRuleOperation {
    @Getter @Setter
    private WebSocketDataDirection dataDirection = WebSocketDataDirection.Server;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = ((WebSocketEventInfo<?>)eventInfo).getDataDirection() == dataDirection;
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Equals, dataDirection, ((WebSocketEventInfo<?>)eventInfo).getDataDirection(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenWebSocketEventDirection> getType() {
        return WhenType.WebSocketEventDirection;
    }
}
