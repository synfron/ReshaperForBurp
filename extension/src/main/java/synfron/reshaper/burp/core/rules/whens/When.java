package synfron.reshaper.burp.core.rules.whens;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.utils.Serializer;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WhenEventDirection.class),
        @JsonSubTypes.Type(value = WhenHasEntity.class),
        @JsonSubTypes.Type(value = WhenMatchesText.class),
        @JsonSubTypes.Type(value = WhenContentType.class),
        @JsonSubTypes.Type(value = WhenMimeType.class),
        @JsonSubTypes.Type(value = WhenProxyName.class),
        @JsonSubTypes.Type(value = WhenMessageType.class),
        @JsonSubTypes.Type(value = WhenInScope.class),
        @JsonSubTypes.Type(value = WhenRepeat.class)
})
public abstract class When<T extends When<T>> implements IRuleOperation<T> {

    @Getter @Setter
    private boolean negate;

    @Getter @Setter
    private boolean useOrCondition;

    public abstract boolean isMatch(EventInfo eventInfo);

    @SuppressWarnings("unchecked")
    public IRuleOperation<?> copy() {
        return Serializer.copy(this);
    }
}
