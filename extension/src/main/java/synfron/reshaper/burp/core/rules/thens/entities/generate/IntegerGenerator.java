package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class IntegerGenerator implements IGenerator {

    private VariableString minValue;

    private VariableString maxValue;

    private VariableString base;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        long minValue = this.minValue.getLong(eventInfo);
        long maxValue = this.maxValue.getLong(eventInfo);
        int base = this.base.getInt(eventInfo);
        String value = ValueGenerator.integer(minValue, maxValue, base);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("minValue", minValue));
            diagnosticProperties.add(Pair.of("maxValue", maxValue));
            diagnosticProperties.add(Pair.of("base", base));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
