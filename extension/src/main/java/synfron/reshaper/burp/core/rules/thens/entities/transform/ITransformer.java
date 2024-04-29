package synfron.reshaper.burp.core.rules.thens.entities.transform;

import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;

import java.io.Serializable;
import java.util.List;

public interface ITransformer {
    String transform(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties);
}
