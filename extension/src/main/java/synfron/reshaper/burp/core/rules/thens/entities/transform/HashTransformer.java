package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.PhraseCase;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class HashTransformer extends Transformer {

    private HashType hashType = HashType.Sha1;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String value = switch (hashType) {
            case Sha1 -> TextUtils.sha1(input);
            case Sha256 -> TextUtils.sha256(input);
            case Sha512 -> TextUtils.sha512(input);
            case Sha256V3 -> TextUtils.sha256V3(input);
            case Sha512V3 -> TextUtils.sha512V3(input);
            case Md5 -> TextUtils.md5(input);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("hashType", hashType));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
