package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class TrimTransformer extends Transformer {

    private TrimOption trimOption = TrimOption.StartAndEnd;
    private VariableString characters;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        String characters = VariableString.getTextOrDefault(eventInfo, this.characters, null);
        String value = switch (trimOption) {
            case Start -> StringUtils.stripStart(input, characters);
            case End -> StringUtils.stripEnd(input, characters);
            case StartAndEnd -> StringUtils.strip(input, characters);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("trimOption", trimOption));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("characters", characters));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
