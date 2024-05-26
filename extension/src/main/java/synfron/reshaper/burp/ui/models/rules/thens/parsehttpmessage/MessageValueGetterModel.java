package synfron.reshaper.burp.ui.models.rules.thens.parsehttpmessage;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage.MessageValueGetter;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.List;

public class MessageValueGetterModel {
    @Getter
    private final MessageValueGetter messageValueGetter;
    @Getter
    private MessageValue sourceMessageValue;
    @Getter
    private String sourceIdentifier = "";
    @Getter
    private GetItemPlacement sourceIdentifierPlacement;
    @Getter
    private VariableSource destinationVariableSource;
    @Getter
    private String destinationVariableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;
    @Getter
    protected boolean validated;
    @Getter
    protected boolean deleted;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public MessageValueGetterModel(MessageValueGetter messageValueGetter) {
        this.messageValueGetter = messageValueGetter;
        sourceMessageValue = messageValueGetter.getSourceMessageValue();
        sourceIdentifier = VariableString.toString(messageValueGetter.getSourceIdentifier(), sourceIdentifier);
        sourceIdentifierPlacement = messageValueGetter.getSourceIdentifierPlacement();
        destinationVariableSource = messageValueGetter.getDestinationVariableSource();
        destinationVariableName = VariableString.toString(messageValueGetter.getDestinationVariableName(), destinationVariableName);
        itemPlacement = messageValueGetter.getItemPlacement();
        delimiter = VariableString.toString(messageValueGetter.getDelimiter(), delimiter);
        index = VariableString.toString(messageValueGetter.getIndex(), index);
    }

    public void setSourceMessageValue(MessageValue sourceMessageValue) {
        this.sourceMessageValue = sourceMessageValue;
        propertyChanged("sourceMessageValue", sourceMessageValue);
    }

    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
        propertyChanged("sourceIdentifier", sourceIdentifier);
    }

    public void setSourceIdentifierPlacement(GetItemPlacement sourceIdentifierPlacement) {
        this.sourceIdentifierPlacement = sourceIdentifierPlacement;
        propertyChanged("sourceIdentifierPlacement", sourceIdentifierPlacement);
    }

    public void setDestinationVariableSource(VariableSource destinationVariableSource) {
        this.destinationVariableSource = destinationVariableSource;
        propertyChanged("destinationVariableSource", destinationVariableSource);
    }

    public void setDestinationVariableName(String destinationVariableName) {
        this.destinationVariableName = destinationVariableName;
        propertyChanged("destinationVariableName", destinationVariableName);
    }

    public void setItemPlacement(SetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
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

    public MessageValueGetterModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(sourceIdentifier) && sourceMessageValue.isIdentifierRequired()) {
            errors.add("Identifier is required");
        }
        if (StringUtils.isEmpty(destinationVariableName)) {
            errors.add("Destination Variable Name is required");
        } else if (!VariableString.isValidVariableName(destinationVariableName)) {
            errors.add("Destination Variable Name is invalid");
        }
        if (destinationVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        messageValueGetter.setSourceMessageValue(sourceMessageValue);
        messageValueGetter.setSourceIdentifier(VariableString.getAsVariableString(sourceIdentifier));
        messageValueGetter.setSourceIdentifierPlacement(sourceIdentifierPlacement);
        messageValueGetter.setDestinationVariableSource(destinationVariableSource);
        messageValueGetter.setDestinationVariableName(VariableString.getAsVariableString(destinationVariableName));
        messageValueGetter.setItemPlacement(itemPlacement);
        messageValueGetter.setDelimiter(VariableString.getAsVariableString(delimiter));
        messageValueGetter.setIndex(VariableString.getAsVariableString(index));
        return true;
    }
}
