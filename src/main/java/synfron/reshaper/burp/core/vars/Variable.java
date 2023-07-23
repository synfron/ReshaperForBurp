package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.utils.TextUtils;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "isList")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Variable.class, name = "false"),
        @JsonSubTypes.Type(value = ListVariable.class, name = "true")
})
public class Variable {
    @Getter
    protected final transient PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter
    protected boolean persistent;

    @Getter
    private Object value;

    @Getter
    protected final String name;

    private Variable() {
        this(null);
    }

    public Variable(String name) {
        this.name = name;
    }

    public Variable withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public void setValue(Object value) {
        this.value = value;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "value", value));
    }

    public void setValue(SetListItemPlacement itemPlacement, String delimiter, Integer index, Object value) {
        this.value = value;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "value", value));
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "persistent", persistent));
    }

    public String toString() {
        return TextUtils.toString(name);
    }

    public boolean hasValue() {
        return value != null;
    }

    public Object getValue(GetListItemPlacement itemPlacement, Integer index) {
        return getValue();
    }

    public boolean isList() {
        return false;
    }
}
