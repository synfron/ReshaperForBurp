package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

@Setter
@Getter
public class HexTransformer extends Transformer {

    private TextTransform action = TextTransform.FromText;
    private VariableString encoding;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String encodingValue = VariableString.getTextOrDefault(eventInfo, encoding, StandardCharsets.ISO_8859_1.displayName());
        Encoder encoder = new Encoder(encodingValue);
        String value = switch (action) {
            case FromText -> TextUtils.toHex(input, encoder);
            case ToText -> TextUtils.fromHex(input, encoder);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("action", action));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("encoder", encoder.getEncoding()));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
