package synfron.reshaper.burp.core.events;

import lombok.Data;

@Data
public class CollectionChangedArgs {
    private final Object source;
    private final CollectionChangedAction action;
    private final Object key;
    private final Object newKey;
    private final Object item;

    public CollectionChangedArgs(Object source, CollectionChangedAction action, Object key, Object item) {
        this(source, action, key, key, item);
    }

    public CollectionChangedArgs(Object source, CollectionChangedAction action, Object key, Object newKey, Object item) {
        this.source = source;
        this.action = action;
        this.key = key;
        this.newKey = newKey;
        this.item = item;
    }
}
