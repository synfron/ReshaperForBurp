package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenSetEncoding extends Then<ThenSetEncoding> {
    @Getter
    @Setter
    private VariableString encoding;

    @Override
    public RuleResponse perform(IEventInfo eventInfo) {
        boolean hasError = false;
        String encodingValue = encoding.getText(eventInfo);
        try {
            eventInfo.getEncoder().setEncoding(encodingValue);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, encodingValue);
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenSetEncoding> getType() {
        return ThenType.SetEncoding;
    }
}
