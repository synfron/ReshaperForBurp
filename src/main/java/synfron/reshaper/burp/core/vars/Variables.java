package synfron.reshaper.burp.core.vars;

import lombok.Getter;
import synfron.reshaper.burp.core.events.*;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Variables {
    @Getter
    protected final CollectionChangedEvent collectionChangedEvent = new CollectionChangedEvent();
    protected HashMap<CaseInsensitiveString, Variable> variables = new HashMap<>();

    public static Variables defaultVariables(Variables variables) {
        return variables != null ? variables : new Variables();
    }

    public List<Variable> getValues() {
        return new ArrayList<>(variables.values());
    }

    public int size() {
        return variables.size();
    }

    public Variable add(String name)
    {
        CaseInsensitiveString key = new CaseInsensitiveString(name);
        boolean hasItem = variables.containsKey(key);
        Variable variable = variables.computeIfAbsent(new CaseInsensitiveString(name), (k) -> new Variable(name));
        if (!hasItem) {
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Add, name, variable));
        }
        return variable;
    }

    public Variable get(String name)
    {
        CaseInsensitiveString key = new CaseInsensitiveString(name);
        if (!variables.containsKey(key))
        {
            throw new IndexOutOfBoundsException("Variable does not exist.");
        }
        return variables.get(key);
    }

    public  Variable getOrDefault(String name)
    {
        return variables.get(new CaseInsensitiveString(name));
    }

    public boolean has(String name)
    {
        return variables.containsKey(new CaseInsensitiveString(name));
    }

    public boolean remove(String name)
    {
        Variable variable = variables.remove(new CaseInsensitiveString(name));
        boolean result = variable != null;
        if (result) {
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Remove, name, variable));
        }
        return result;
    }

}
