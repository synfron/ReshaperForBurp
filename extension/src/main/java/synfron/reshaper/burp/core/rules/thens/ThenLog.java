package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenLog extends Then<ThenLog> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter
    @Setter
    private VariableString text;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        try {

            Log.get().withMessage(text.getText(eventInfo)).log();
            hasError = false;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, VariableString.getTextOrDefault(eventInfo, text, null));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenLog> getType() {
        return ThenType.Log;
    }
}
