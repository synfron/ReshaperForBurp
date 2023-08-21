package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.*;

public class ThenDeleteVariable extends Then<ThenDeleteVariable> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter @Setter
    private VariableSource targetSource = VariableSource.Global;
    @Getter @Setter
    private VariableString variableName;
    @Getter @Setter
    private DeleteListItemPlacement itemPlacement = DeleteListItemPlacement.All;
    @Getter @Setter
    private VariableString index;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        try {
            Variables variables = getVariables(targetSource, eventInfo);
            if (targetSource.isList() && itemPlacement != DeleteListItemPlacement.All) {
                String listName = variableName.getText(eventInfo);
                ListVariable listVariable = (ListVariable) variables.getOrDefault(Variables.asKey(listName, true));
                if (listVariable != null) {
                    listVariable.delete(itemPlacement, VariableString.getIntOrDefault(eventInfo, index, null));
                }
            } else {
                variables.remove(Variables.asKey(variableName.getText(eventInfo), targetSource.isList()));
            }
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(
                    this,
                    hasError,
                    VariableSourceEntry.getTag(
                            targetSource,
                            variableName.getText(eventInfo),
                            IListItemPlacement.toGet(itemPlacement),
                            VariableString.getIntOrDefault(eventInfo, index, null)
                    )
            );
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenDeleteVariable> getType() {
        return ThenType.DeleteVariable;
    }
}
