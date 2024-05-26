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
public class UuidGenerator implements IGenerator {

    private ValueGenerator.UuidVersion version = ValueGenerator.UuidVersion.V4;

    private VariableString namespace;

    private VariableString name;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String namespace = VariableString.getTextOrDefault(eventInfo, this.namespace, null);
        String name = VariableString.getTextOrDefault(eventInfo, this.name, null);
        String value = ValueGenerator.uuid(version, namespace, name);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("version", version));
            diagnosticProperties.add(Pair.of("namespace", namespace));
            diagnosticProperties.add(Pair.of("name", name));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }

}
