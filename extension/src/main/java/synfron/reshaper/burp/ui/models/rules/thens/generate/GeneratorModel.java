package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class GeneratorModel<T extends GeneratorModel<T, G>, G extends IGenerator> {
    protected final G generator;
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    protected boolean validated;

    public GeneratorModel(G generator) {
        this.generator = generator;
    }

    protected void propertyChanged(String name, Object value) {
        setValidated(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public T withListener(IEventListener<PropertyChangedArgs> listener) {
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

    public boolean persist() {
        return validate().isEmpty();
    }
}
