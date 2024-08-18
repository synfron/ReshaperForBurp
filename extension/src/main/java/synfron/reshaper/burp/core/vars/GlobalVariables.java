package synfron.reshaper.burp.core.vars;

import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.core.events.CollectionChangedAction;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalVariables extends Variables {

    public List<Variable> exportVariables() {
        return variables.values().stream()
                .filter(Variable::isPersistent)
                .collect(Collectors.toList());
    }

    public void importVariables(List<Variable> variables, boolean overwriteDuplicates) {
        variables = ObjectUtils.defaultIfNull(variables, Collections.emptyList());
        variables.forEach(variable -> {
            if (overwriteDuplicates) {
                this.variables.put(Variables.asKey(variable.getName(), variable.isList()), variable);
            } else {
                this.variables.computeIfAbsent(Variables.asKey(variable.getName(), variable.isList()), name -> variable);
            }
        });
        collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Reset));
    }
}
