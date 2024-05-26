package synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
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
    private SetItemPlacement destinationIdentifierPlacement;
    @Getter
    protected boolean validated;
    @Getter
    protected boolean deleted;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public MessageValueSetterModel(MessageValueSetter messageValueSetter) {
        this.messageValueSetter = messageValueSetter;
        sourceText = VariableString.toString(messageValueSetter.getSourceText(), sourceText);
        destinationMessageValue = messageValueSetter.getDestinationMessageValue();
        destinationIdentifier = VariableString.toString(messageValueSetter.getDestinationIdentifier(), destinationIdentifier);
        destinationIdentifierPlacement = messageValueSetter.getDestinationIdentifierPlacement();
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

    public void setDestinationIdentifierPlacement(SetItemPlacement destinationIdentifierPlacement) {
        this.destinationIdentifierPlacement = destinationIdentifierPlacement;
        propertyChanged("destinationIdentifierPlacement", destinationIdentifierPlacement);
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
            errors.add("Destination Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        messageValueSetter.setDestinationMessageValue(destinationMessageValue);
        messageValueSetter.setDestinationIdentifier(VariableString.getAsVariableString(destinationIdentifier));
        messageValueSetter.setDestinationIdentifierPlacement(destinationIdentifierPlacement);
        messageValueSetter.setSourceText(VariableString.getAsVariableString(sourceText));
        return true;
    }
}
