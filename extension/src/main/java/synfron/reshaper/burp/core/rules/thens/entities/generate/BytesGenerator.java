package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Setter
@Getter
public class BytesGenerator implements IGenerator {

    private VariableString length;
    private VariableString encoding;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        int length = this.length.getInt(eventInfo);
        String encodingValue = VariableString.getTextOrDefault(eventInfo, encoding, StandardCharsets.ISO_8859_1.displayName());
        Encoder encoder = new Encoder(encodingValue);
        String value = ValueGenerator.bytes(length, encoder);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("length", length));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
