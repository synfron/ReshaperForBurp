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
public class WordGenerator implements IGenerator {

    private WordGeneratorType generatorType = WordGeneratorType.Paragraphs;

    private VariableString count;

    private VariableString separator;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        int count = VariableString.getIntOrDefault(eventInfo, this.count, 1);
        String separator = VariableString.getTextOrDefault(eventInfo, this.separator, "\n");
        String value = switch (generatorType) {
            case Words -> ValueGenerator.words(count, separator, false);
            case Sentences -> ValueGenerator.sentences(count, separator);
            case Paragraphs -> ValueGenerator.paragraphs(count, separator);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("generatorType", generatorType));
            diagnosticProperties.add(Pair.of("count", count));
            diagnosticProperties.add(Pair.of("separator", separator));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
