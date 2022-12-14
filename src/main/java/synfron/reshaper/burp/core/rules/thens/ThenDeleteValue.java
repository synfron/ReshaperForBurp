package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.DeleteItemPlacement;
import synfron.reshaper.burp.core.utils.IItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenDeleteValue extends Then<ThenDeleteValue> implements IHttpRuleOperation {
    @Getter
    @Setter
    private MessageValue messageValue;
    @Getter
    @Setter
    private VariableString identifier;
    @Getter
    @Setter
    private DeleteItemPlacement identifierPlacement = DeleteItemPlacement.Last;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        try {
            MessageValueHandler.setValue(eventInfo, messageValue, identifier, IItemPlacement.toSet(identifierPlacement), null);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, messageValue, VariableString.getTextOrDefault(eventInfo, identifier, null));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDeleteValue> getType() {
        return ThenType.DeleteValue;
    }
}
