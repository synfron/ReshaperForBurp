package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Setter
@Getter
public class JwtDecodeTransformer extends Transformer {

    private JwtSegment segment = JwtSegment.Header;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String value = StringUtils.isNotEmpty(input) ? switch (segment) {
            case Header -> TextUtils.base64UrlDecode(input.split("\\.", 3)[0], new Encoder(StandardCharsets.UTF_8.displayName()));
            case Payload -> TextUtils.base64UrlDecode(CollectionUtils.elementAtOrDefault(input.split("\\.", 3), 1), new Encoder(StandardCharsets.UTF_8.displayName()));
            case Signature -> TextUtils.base64UrlDecode(CollectionUtils.elementAtOrDefault(input.split("\\.", 3), 2), new Encoder(StandardCharsets.ISO_8859_1.displayName()));
        } : "";
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("segment", segment));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
