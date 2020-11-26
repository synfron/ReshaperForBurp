package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

public class ThenSetVariable extends ThenSet<ThenSetVariable> {

    @Getter @Setter
    private VariableSource targetSource;
    @Getter @Setter
    private VariableString variableName;

    public RuleResponse perform(EventInfo eventInfo)
    {
        String replacementText = getReplacementValue(eventInfo);
        setValue(eventInfo, replacementText);
        return RuleResponse.Continue;
    }

    private void setValue(EventInfo eventInfo, String replacementText)
    {
        Variables variables = null;
        switch (targetSource)
        {
            case Event:
                variables = eventInfo.getVariables();
                break;
            case Global:
                variables = GlobalVariables.get();
                break;
        }
        if (variables != null)
        {
            Variable variable = variables.add(variableName.getText(eventInfo.getVariables()));
            if (destinationMessageValuePath != null && destinationMessageValueType != MessageValueType.Text && variable.getValue() != null)
            {
                switch (destinationMessageValueType)
                {
                    case Json:
                        variable.setValue(TextUtils.setJsonValue(variable.getValue().toString(), destinationMessageValuePath.getText(eventInfo.getVariables()), replacementText));
                        break;
                    case Html:
                        variable.setValue(TextUtils.setHtmlValue(variable.getValue().toString(), destinationMessageValuePath.getText(eventInfo.getVariables()), replacementText));
                        break;
                }
            } else {
                variable.setValue(replacementText);
            }
        }
    }

    @Override
    public RuleOperationType<ThenSetVariable> getType() {
        return ThenType.SetVariable;
    }
}
