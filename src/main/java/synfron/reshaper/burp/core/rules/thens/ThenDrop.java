package synfron.reshaper.burp.core.rules.thens;

import burp.IInterceptedProxyMessage;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenDrop extends Then<ThenDrop> {
    @Getter @Setter
    private boolean dropMessage = true;

    public RuleResponse perform(EventInfo eventInfo) {
        if (dropMessage) {
            eventInfo.getProxyMessage().setInterceptAction(IInterceptedProxyMessage.ACTION_DROP);
        } else if (eventInfo.getProxyMessage().getInterceptAction() == IInterceptedProxyMessage.ACTION_DROP) {
            eventInfo.getProxyMessage().setInterceptAction(IInterceptedProxyMessage.ACTION_FOLLOW_RULES);
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDrop> getType() {
        return ThenType.Drop;
    }
}
