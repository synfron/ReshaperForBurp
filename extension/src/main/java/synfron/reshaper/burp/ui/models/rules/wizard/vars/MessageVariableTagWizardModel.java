package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;

import java.util.ArrayList;
import java.util.List;

public class MessageVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private MessageValue messageValue = MessageValue.HttpRequestBody;

    @Getter
    private String identifier;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (messageValue.isIdentifierRequired() && StringUtils.isEmpty(identifier)) {
            errors.add("Identifier is required");
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Message;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableTag.getShortTag(
                        VariableSource.Message,
                        messageValue.name().toLowerCase(),
                        messageValue.isIdentifierRequired() ? identifier : null
                ) :
                null;
    }
}
