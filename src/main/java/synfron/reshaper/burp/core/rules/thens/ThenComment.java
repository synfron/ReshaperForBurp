package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenComment extends Then<ThenComment> {

    @Getter @Setter
    private VariableString text;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = true;
        try {
            eventInfo.getRequestResponse().setComment(text.getText(eventInfo));
            hasError = false;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, VariableString.getTextOrDefault(eventInfo, text, null));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenComment> getType() {
        return ThenType.Comment;
    }
}
