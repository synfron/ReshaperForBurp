package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;

import java.util.Objects;

public class Variable {
    @Getter
    private final transient PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter
    private boolean persistent;

    @Getter
    private Object value;

    @Getter
    private final String name;

    @JsonCreator
    public Variable(@JsonProperty("name") String name) {
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

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "persistent", persistent));
    }

    public String toString() {
        return Objects.toString(name);
    }
}
