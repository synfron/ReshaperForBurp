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
public class CaseTransformer extends Transformer {

    private PhraseCase phraseCase = PhraseCase.UpperCase;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String value = TextUtils.changeCase(input, phraseCase);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("phraseCase", phraseCase));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
