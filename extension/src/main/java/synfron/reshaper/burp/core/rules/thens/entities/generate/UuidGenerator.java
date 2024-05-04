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

    @Getter
    public enum UuidVersion {
        V3(true),
        V4(false),
        V5(true);

        private final boolean hasInputs;

        UuidVersion(boolean hasInputs) {
            this.hasInputs = hasInputs;
        }
    }

    private UuidVersion version = UuidVersion.V4;

    private VariableString namespace;

    private VariableString name;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String namespace = VariableString.getTextOrDefault(eventInfo, this.namespace, null);
        String name = VariableString.getTextOrDefault(eventInfo, this.name, null);
        String value = switch (version) {
            case V3 -> ValueGenerator.uuidV3(namespace, name);
            case V4 -> ValueGenerator.uuidV4();
            case V5 -> ValueGenerator.uuidV5(namespace, name);
        };
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("version", version));
            diagnosticProperties.add(Pair.of("namespace", namespace));
            diagnosticProperties.add(Pair.of("name", name));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
