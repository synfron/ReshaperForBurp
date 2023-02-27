package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenSendMessage extends Then<ThenSendMessage> implements IWebSocketRuleOperation {

    @Getter
    @Setter
    private WebSocketDataDirection dataDirection = WebSocketDataDirection.Server;

    @Getter
    @Setter
    private WebSocketMessageType messageType = WebSocketMessageType.Text;
    @Getter
    @Setter
    private VariableString message;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        String value = null;
        try {
            WebSocketEventInfo<?> webSocketEventInfo = (WebSocketEventInfo<?>)eventInfo;
            webSocketEventInfo.getMessageSender().send(webSocketEventInfo, dataDirection, messageType, value = message.getText(eventInfo));
            hasError = false;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, value);
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenSendMessage> getType() {
        return ThenType.SendMessage;
    }
}
