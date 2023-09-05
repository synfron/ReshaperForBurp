package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenMessageType extends When<WhenMessageType> implements IWebSocketRuleOperation {
    @Getter @Setter
    private WebSocketMessageType messageType = WebSocketMessageType.Text;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = ((WebSocketEventInfo<?>)eventInfo).getMessageType() == messageType;
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Equals, messageType, ((WebSocketEventInfo<?>)eventInfo).getMessageType(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenMessageType> getType() {
        return WhenType.MessageType;
    }
}
