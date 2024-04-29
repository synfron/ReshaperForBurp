package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class UnixTimestampGenerator implements IGenerator {

    private VariableString format;

    private VariableString minTimestamp;

    private VariableString maxTimestamp;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String format = VariableString.getTextOrDefault(eventInfo, this.format, null);
        String minTimestamp = VariableString.getTextOrDefault(eventInfo, this.minTimestamp, null);
        String maxTimestamp = VariableString.getTextOrDefault(eventInfo, this.maxTimestamp, null);
        String value = StringUtils.isAllEmpty(minTimestamp, maxTimestamp) ? ValueGenerator.timestamp(minTimestamp, maxTimestamp, format) : ValueGenerator.currentTimestamp();
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("format", format));
            diagnosticProperties.add(Pair.of("minTimestamp", minTimestamp));
            diagnosticProperties.add(Pair.of("maxTimestamp", maxTimestamp));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
