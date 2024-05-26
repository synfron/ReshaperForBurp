package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.ValueGenerator;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class IpAddressGenerator implements IGenerator {

    private ValueGenerator.IpVersion version = ValueGenerator.IpVersion.V4;

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        String value = ValueGenerator.ipAddress(version);
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("version", version));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }

}
