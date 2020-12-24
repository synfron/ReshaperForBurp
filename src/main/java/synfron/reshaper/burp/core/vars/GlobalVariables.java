package synfron.reshaper.burp.core.vars;

import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.core.events.CollectionChangedAction;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalVariables extends Variables {

    private GlobalVariables() {}

    private static final GlobalVariables global = new GlobalVariables();

    public static GlobalVariables get() {
        return global;
    }

    public List<Variable> exportVariables() {
        return variables.values().stream()
                .filter(Variable::isPersistent)
                .collect(Collectors.toList());
    }

    public void importVariables(List<Variable> variables, boolean overrideDuplicates) {
        variables = ObjectUtils.defaultIfNull(variables, Collections.emptyList());
        variables.forEach(variable -> {
            if (overrideDuplicates) {
                this.variables.put(new CaseInsensitiveString(variable.getName()), variable);
            } else {
                this.variables.computeIfAbsent(new CaseInsensitiveString(variable.getName()), name -> variable);
            }
        });
        collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Reset));
    }
}
