package synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.List;

public class MessageValueSetterModel {
    @Getter
    private final MessageValueSetter messageValueSetter;
    @Getter
    private String sourceText = "";
    @Getter
    private MessageValue destinationMessageValue;
    @Getter
    private String destinationIdentifier = "";
    @Getter
    protected boolean validated;
    @Getter
    protected boolean deleted;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public MessageValueSetterModel(MessageValueSetter messageValueSetter) {
        this.messageValueSetter = messageValueSetter;
        sourceText = VariableString.getTag(messageValueSetter.getSourceText(), sourceText);
        destinationMessageValue = messageValueSetter.getDestinationMessageValue();
        destinationIdentifier = VariableString.getTag(messageValueSetter.getDestinationIdentifier(), destinationIdentifier);
    }

    public void setDestinationMessageValue(MessageValue destinationMessageValue) {
        this.destinationMessageValue = destinationMessageValue;
        propertyChanged("destinationMessageValue", destinationMessageValue);
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
        propertyChanged("sourceText", sourceText);
    }

    public void setDestinationIdentifier(String destinationIdentifier) {
        this.destinationIdentifier = destinationIdentifier;
        propertyChanged("destinationIdentifier", destinationIdentifier);
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

    public MessageValueSetterModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(destinationIdentifier) && destinationMessageValue.isIdentifierRequired()) {
            errors.add("Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        messageValueSetter.setDestinationMessageValue(destinationMessageValue);
        messageValueSetter.setDestinationIdentifier(VariableString.getAsVariableString(destinationIdentifier));
        messageValueSetter.setSourceText(VariableString.getAsVariableString(sourceText));
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
