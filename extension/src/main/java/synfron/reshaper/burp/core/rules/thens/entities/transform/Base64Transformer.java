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
import java.util.List;

@Setter
@Getter
public class Base64Transformer extends Transformer {

    private EncodeTransform action = EncodeTransform.Encode;
    private Base64Variant variant = Base64Variant.Standard;
    private VariableString encoding;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String encodingValue = VariableString.getTextOrDefault(eventInfo, encoding, StandardCharsets.ISO_8859_1.displayName());
        Encoder encoder = new Encoder(encodingValue);
        String value = switch (variant) {
            case Standard -> switch (action) {
                case Encode -> TextUtils.base64Encode(input, encoder);
                case Decode -> TextUtils.base64Decode(input, encoder);
            };
            case Url -> switch (action) {
                case Encode -> TextUtils.base64UrlEncode(input, encoder);
                case Decode -> TextUtils.base64UrlDecode(input, encoder);
            };
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("action", action));
            diagnosticProperties.add(Pair.of("variant", variant));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("encoder", encoder.getEncoding()));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
