package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenDelay extends Then<ThenDelay> {
    @Getter
    @Setter
    private VariableString delay;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        try {
            Thread.sleep(ObjectUtils.defaultIfNull(delay.getInt(eventInfo.getVariables()), 0));
        } catch (InterruptedException e) {
            Log.get().withMessage("Delay interrupted").withException(e).log();
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDelay> getType() {
        return ThenType.Delay;
    }
}
