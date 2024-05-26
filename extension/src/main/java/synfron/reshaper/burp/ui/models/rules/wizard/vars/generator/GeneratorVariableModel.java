package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class GeneratorVariableModel<T extends GeneratorVariableModel<T>> {
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    protected boolean validated;

    protected void propertyChanged(String name, Object value) {
        setValidated(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    protected T withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return (T)this;
    }

    protected void setValidated(boolean validated) {
        if (validated != this.validated) {
            this.validated = validated;
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "validated", validated));
        }
    }

    public List<String> validate() {
        return new ArrayList<>();
    }


    public String getTag() {
        return validate().isEmpty() ?
                getTagInternal() :
                null;
    }

    public abstract String getTagInternal();
}
