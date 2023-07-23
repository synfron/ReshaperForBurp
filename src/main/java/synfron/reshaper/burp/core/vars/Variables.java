package synfron.reshaper.burp.core.vars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import synfron.reshaper.burp.core.events.*;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Variables {
    @Getter
    protected final CollectionChangedEvent collectionChangedEvent = new CollectionChangedEvent();
    protected HashMap<Key, Variable> variables = new HashMap<>();

    public static Variables defaultVariables(Variables variables) {
        return variables != null ? variables : new Variables();
    }

    public List<Variable> getValues() {
        return new ArrayList<>(variables.values());
    }

    public int size() {
        return variables.size();
    }

    public Variable add(Key key)
    {
        boolean hasItem = variables.containsKey(key);
        Variable variable = variables.computeIfAbsent(key, (k) -> key.isList() ?
                new ListVariable(key.getName().getValue()) :
                new Variable(key.getName().getValue())
        );
        if (!hasItem) {
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Add, key, variable));
        }
        return variable;
    }

    public Variable get(Key key)
    {
        if (!variables.containsKey(key))
        {
            throw new IndexOutOfBoundsException("Variable does not exist.");
        }
        return variables.get(key);
    }

    public Variable getOrDefault(Key key)
    {
        return variables.get(key);
    }

    public boolean has(Key key)
    {
        return variables.containsKey(key);
    }

    public boolean remove(Key key)
    {
        Variable variable = variables.remove(key);
        boolean result = variable != null;
        if (result) {
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Remove, key, variable));
        }
        return result;
    }

    public static Key asKey(String name, boolean isList) {
        return new Key(new CaseInsensitiveString(name), isList);
    }

    @Data
    @AllArgsConstructor
    public static class Key {
        private CaseInsensitiveString name;
        private boolean isList;

        @Override
        public String toString() {
            return name + (isList ? "[]" : "");
        }
    }
}
