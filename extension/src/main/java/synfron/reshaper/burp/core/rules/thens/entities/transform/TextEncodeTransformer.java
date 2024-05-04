package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.utils.UrlUtils;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class TextEncodeTransformer extends Transformer {

    private EncoderType encoderType = EncoderType.Html;
    private EncodeTransform action = EncodeTransform.Encode;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String value = switch (encoderType) {
            case Html -> action == EncodeTransform.Encode ? TextUtils.htmlEncode(input) : TextUtils.htmlDecode(input);
            case Xml -> action == EncodeTransform.Encode ? TextUtils.xmlEncode(input) : TextUtils.xmlDecode(input);
            case Json -> action == EncodeTransform.Encode ? TextUtils.jsonEscape(input) : TextUtils.jsonUnescape(input);
            case Url -> action == EncodeTransform.Encode ? UrlUtils.urlEncode(input) : UrlUtils.urlDecode(input);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("encoderType", encoderType));
            diagnosticProperties.add(Pair.of("action", action));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
