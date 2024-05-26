package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class IntegerTransformer extends Transformer {

    private VariableString sourceBase;
    private VariableString targetBase;

    @Override
    public String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String input = this.input.getText(eventInfo);
        int sourceBase = VariableString.getIntOrDefault(eventInfo, this.sourceBase, 10);
        int targetBase = VariableString.getIntOrDefault(eventInfo, this.targetBase, 10);
        String value = TextUtils.changeBase(input, sourceBase, targetBase);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("sourceBase", sourceBase));
            diagnosticProperties.add(Pair.of("targetBase", targetBase));
            diagnosticProperties.add(Pair.of("input", input));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
