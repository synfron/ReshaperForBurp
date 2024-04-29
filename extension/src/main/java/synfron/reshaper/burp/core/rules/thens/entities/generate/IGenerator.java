package synfron.reshaper.burp.core.rules.thens.entities.generate;

import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;

import java.io.Serializable;
import java.util.List;

public interface IGenerator {
    String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties);
}
