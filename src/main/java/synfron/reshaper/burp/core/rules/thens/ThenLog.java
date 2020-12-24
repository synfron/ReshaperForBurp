package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenLog extends Then<ThenLog> {
    @Getter
    @Setter
    private VariableString text;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        Log.get().withMessage(text.getText(eventInfo)).log();
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenLog> getType() {
        return ThenType.Log;
    }
}
