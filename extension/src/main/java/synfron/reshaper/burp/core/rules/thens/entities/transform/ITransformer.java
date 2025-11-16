package synfron.reshaper.burp.core.rules.thens.entities.transform;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.List;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Base64Transformer.class),
        @JsonSubTypes.Type(value = CaseTransformer.class),
        @JsonSubTypes.Type(value = EscapeTransformer.class),
        @JsonSubTypes.Type(value = HashTransformer.class),
        @JsonSubTypes.Type(value = HexTransformer.class),
        @JsonSubTypes.Type(value = IntegerTransformer.class),
        @JsonSubTypes.Type(value = JwtDecodeTransformer.class),
        @JsonSubTypes.Type(value = TrimTransformer.class)
})
public interface ITransformer {
    String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties);

    VariableString getInput();

    void setInput(VariableString input);

    @JsonProperty(value = "@class", index = 0)
    default String getJid() {
        return "." + getClass().getSimpleName();
    }
}
