package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.PasswordCharacterGroups;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Setter
@Getter
public class PasswordGenerator implements IGenerator {

    private VariableString minLength;

    private VariableString maxLength;

    EnumSet<PasswordCharacterGroups> characterGroups = EnumSet.allOf(PasswordCharacterGroups.class);

    @Override
    public String generate(EventInfo eventInfo, List<Pair<String, ? extends Serializable>> diagnosticProperties) {
        int minLength = this.minLength.getInt(eventInfo);
        int maxLength = this.maxLength.getInt(eventInfo);
        String value = ValueGenerator.password(minLength, maxLength, new ArrayList<>(characterGroups));
        if (diagnosticProperties != null) {
            diagnosticProperties.add(Pair.of("minLength", minLength));
            diagnosticProperties.add(Pair.of("maxLength", maxLength));
            diagnosticProperties.add(Pair.of("characterGroups", characterGroups));
            diagnosticProperties.add(Pair.of("value", value));
        }
        return value;
    }
}
