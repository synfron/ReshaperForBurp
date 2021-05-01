package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
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
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        try {
            Variables variables = null;
            switch (targetSource) {
                case Event:
                    variables = eventInfo.getVariables();
                    break;
                case Global:
                    variables = GlobalVariables.get();
                    break;
            }
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
