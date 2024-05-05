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
public class EscapeTransformer extends Transformer {

    private EntityType entityType = EntityType.Html;
    private EscapeTransform action = EscapeTransform.Escape;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String value = switch (entityType) {
            case Html -> action == EscapeTransform.Escape ? TextUtils.htmlEncode(input) : TextUtils.htmlDecode(input);
            case Xml -> action == EscapeTransform.Escape ? TextUtils.xmlEncode(input) : TextUtils.xmlDecode(input);
            case Json -> action == EscapeTransform.Escape ? TextUtils.jsonEscape(input) : TextUtils.jsonUnescape(input);
            case Url -> action == EscapeTransform.Escape ? UrlUtils.urlEncode(input) : UrlUtils.urlDecode(input);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("entityType", entityType));
            diagnosticProperties.add(Pair.of("action", action));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
