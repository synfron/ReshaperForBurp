package synfron.reshaper.burp.ui.models.rules.thens.buildmessage;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.thens.entities.buildmessage.MessageSetter;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.List;

public class MessageSetterModel {
    @Getter
    private final MessageSetter messageSetter;
    @Getter
    private String text;
    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier = "";
    @Getter
    protected boolean validated;
    @Getter
    protected boolean deleted;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public MessageSetterModel(MessageSetter messageSetter) {
        this.messageSetter = messageSetter;
        text = VariableString.getFormattedString(messageSetter.getText(), text);
        messageValue = messageSetter.getMessageValue();
        identifier = VariableString.getFormattedString(messageSetter.getIdentifier(), identifier);
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        propertyChanged("deleted", deleted);
    }

    protected void setValidated(boolean validated) {
        if (validated != this.validated) {
            this.validated = validated;
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "validated", validated));
        }
    }

    protected void propertyChanged(String name, Object value) {
        setValidated(false);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public MessageSetterModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(identifier) && MessageValueHandler.hasIdentifier(messageValue)) {
            errors.add("Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        messageSetter.setMessageValue(messageValue);
        messageSetter.setIdentifier(VariableString.getAsVariableString(identifier));
        messageSetter.setText(VariableString.getAsVariableString(text));
        return true;
    }

    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }
}
