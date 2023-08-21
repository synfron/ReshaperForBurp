package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;

public class ThenDelay extends Then<ThenDelay> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter
    @Setter
    private VariableString delay;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        try {
            Thread.sleep(ObjectUtils.defaultIfNull(delay.getInt(eventInfo), 0));
        } catch (InterruptedException e) {
            Log.get().withMessage("Delay interrupted").withException(e).logErr();
            hasError = true;
            throw new WrappedException(e);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(Pair.of("millis", VariableString.getTextOrDefault(eventInfo, delay, "0"))));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDelay> getType() {
        return ThenType.Delay;
    }
}
