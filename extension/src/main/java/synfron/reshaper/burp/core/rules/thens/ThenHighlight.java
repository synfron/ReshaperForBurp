package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HighlightColor;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenHighlight extends Then<ThenHighlight> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter
    @Setter
    private HighlightColor color = HighlightColor.None;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        try {
            if (eventInfo.getAnnotations() != null) {
                eventInfo.getAnnotations().setHighlightColor(color.getHighlightColor());
                hasError = false;
            }
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, color.getValue());
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenHighlight> getType() {
        return ThenType.Highlight;
    }

}
