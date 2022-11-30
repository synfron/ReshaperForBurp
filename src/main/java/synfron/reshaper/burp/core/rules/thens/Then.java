package synfron.reshaper.burp.core.rules.thens;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.Variables;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ThenBreak.class),
        @JsonSubTypes.Type(value = ThenDelay.class),
        @JsonSubTypes.Type(value = ThenDeleteValue.class),
        @JsonSubTypes.Type(value = ThenDeleteVariable.class),
        @JsonSubTypes.Type(value = ThenDrop.class),
        @JsonSubTypes.Type(value = ThenHighlight.class),
        @JsonSubTypes.Type(value = ThenComment.class),
        @JsonSubTypes.Type(value = ThenLog.class),
        @JsonSubTypes.Type(value = ThenRunRules.class),
        @JsonSubTypes.Type(value = ThenRunScript.class),
        @JsonSubTypes.Type(value = ThenEvaluate.class),
        @JsonSubTypes.Type(value = ThenSendTo.class),
        @JsonSubTypes.Type(value = ThenSetEventDirection.class),
        @JsonSubTypes.Type(value = ThenSetValue.class),
        @JsonSubTypes.Type(value = ThenSetVariable.class),
        @JsonSubTypes.Type(value = ThenRunProcess.class),
        @JsonSubTypes.Type(value = ThenBuildHttpMessage.class),
        @JsonSubTypes.Type(value = ThenParseHttpMessage.class),
        @JsonSubTypes.Type(value = ThenSendRequest.class)
})
public abstract class Then<T extends Then<T>> implements IRuleOperation<T> {

    public abstract RuleResponse perform(EventInfo eventInfo);

    @SuppressWarnings("unchecked")
    public IRuleOperation<?> copy() {
        return Serializer.copy(this);
    }

    protected void setVariable(VariableSource variableSource, EventInfo eventInfo, String variableName, String value) {
        Variables variables = getVariables(variableSource, eventInfo);
        if (variables != null) {
            Variable variable = variables.add(variableName);
            variable.setValue(value);
        }
    }
}
