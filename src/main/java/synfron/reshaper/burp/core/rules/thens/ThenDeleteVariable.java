package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.Variables;

public class ThenDeleteVariable extends Then<ThenDeleteVariable> {

    @Getter @Setter
    private VariableSource targetSource = VariableSource.Global;
    @Getter @Setter
    private VariableString variableName;

    @Override
    public RuleResponse perform(IEventInfo eventInfo) {
        boolean hasError = false;
        try {
            Variables variables = switch (targetSource) {
                case Event -> eventInfo.getVariables();
                case Global -> GlobalVariables.get();
                default -> null;
            };
            variables.remove(variableName.getText(eventInfo));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, targetSource, VariableString.getTextOrDefault(eventInfo, variableName, null));
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDeleteVariable> getType() {
        return ThenType.DeleteVariable;
    }
}
