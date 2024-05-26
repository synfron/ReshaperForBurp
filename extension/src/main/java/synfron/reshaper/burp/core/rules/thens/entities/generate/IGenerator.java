package synfron.reshaper.burp.core.rules.thens.entities.generate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BytesGenerator.class),
        @JsonSubTypes.Type(value = IntegerGenerator.class),
        @JsonSubTypes.Type(value = IpAddressGenerator.class),
        @JsonSubTypes.Type(value = PasswordGenerator.class),
        @JsonSubTypes.Type(value = TimestampGenerator.class),
        @JsonSubTypes.Type(value = UnixTimestampGenerator.class),
        @JsonSubTypes.Type(value = UuidGenerator.class),
        @JsonSubTypes.Type(value = WordGenerator.class)
})
public interface IGenerator {
    String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties);
}
