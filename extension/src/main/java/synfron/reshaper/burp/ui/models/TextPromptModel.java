package synfron.reshaper.burp.ui.models;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.ui.utils.IPrompterModel;

@Getter
public class TextPromptModel implements IPrompterModel<TextPromptModel> {

    private final String description;
    private String text;
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private boolean dismissed;
    private boolean invalidated = true;

    public TextPromptModel(String description, String text) {
        this.description = description;
        this.text = text;
    }

    public TextPromptModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    @Override
    public boolean submit() {
        setInvalidated(false);
        setDismissed(true);
        return true;
    }

    @Override
    public boolean cancel() {
        setDismissed(true);
        return true;
    }
}
