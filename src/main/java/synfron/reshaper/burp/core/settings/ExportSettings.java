package synfron.reshaper.burp.core.settings;

import lombok.Data;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.vars.Variable;

import java.util.Collections;
import java.util.List;

@Data
public class ExportSettings {
    private List<Rule> rules = Collections.emptyList();
    private List<Variable> variables = Collections.emptyList();
}
