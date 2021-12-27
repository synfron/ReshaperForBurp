package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.DeleteItemPlacement;
import synfron.reshaper.burp.core.utils.IItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;

public class ThenDeleteValue extends Then<ThenDeleteValue> {
    @Getter
    @Setter
    private MessageValue messageValue = MessageValue.HttpRequestBody;
    @Getter
    @Setter
    private VariableString identifier;
    @Getter
    @Setter
    private DeleteItemPlacement identifierPlacement = DeleteItemPlacement.Last;

    @Override
    public RuleResponse perform(IEventInfo eventInfo) {
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
