package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.thens.entities.transform.ITransformer;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class TransformerModel<T extends TransformerModel<T, R>, R extends ITransformer> {
    protected final R transformer;

    private String input = "";
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    protected boolean validated;

    public TransformerModel(R transformer) {
        this.transformer = transformer;
        input = VariableString.toString(transformer.getInput(), input);
    }

    public void setInput(String input) {
        this.input = input;
        propertyChanged("input", input);
    }

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
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(input)) {
            errors.add("Input is required");
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        transformer.setInput(VariableString.getAsVariableString(input));
        return true;
    }
}
