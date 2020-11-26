package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.core.settings.Settings;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;

import java.util.Map;
import java.util.stream.Collectors;

public class GlobalVariables extends Variables {

    private GlobalVariables() {}

    private static final GlobalVariables global = new GlobalVariables();

    public static GlobalVariables get() {
        return global;
    }

    public void save()
    {
        Map<CaseInsensitiveString, Variable> persistables = variables.entrySet().stream()
                .filter(variable -> variable.getValue().isPersistent())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Settings.store("Reshaper.variables", persistables);
    }

    public void load() {
        variables = ObjectUtils.defaultIfNull(Settings.get("Reshaper.variables", new TypeReference<>() {}), variables);
    }
}
