package synfron.reshaper.burp.core.rules.thens;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.rules.RuleResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ThenBreak.class),
        @JsonSubTypes.Type(value = ThenDelay.class),
        @JsonSubTypes.Type(value = ThenDeleteValue.class),
        @JsonSubTypes.Type(value = ThenDeleteVariable.class),
        @JsonSubTypes.Type(value = ThenDrop.class),
        @JsonSubTypes.Type(value = ThenHighlight.class),
        @JsonSubTypes.Type(value = ThenLog.class),
        @JsonSubTypes.Type(value = ThenRunRules.class),
        @JsonSubTypes.Type(value = ThenRunScript.class),
        @JsonSubTypes.Type(value = ThenSendTo.class),
        @JsonSubTypes.Type(value = ThenSetEventDirection.class),
        @JsonSubTypes.Type(value = ThenSetValue.class),
        @JsonSubTypes.Type(value = ThenSetVariable.class)
})
public abstract class Then<T extends Then<T>> implements IRuleOperation<T> {
    public abstract RuleResponse perform(EventInfo eventInfo);

    @SuppressWarnings("unchecked")
    public T copy() {
        try {
            return (T)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new WrappedException(e);
        }
    }
}
