package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;

import java.util.ArrayList;
import java.util.List;

public class MacroVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private String macroItemNumber;

    @Getter
    private MessageValue messageValue = MessageValue.HttpRequestBody;

    @Getter
    private String identifier;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public MacroVariableTagWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    public void setMacroItemNumber(String macroItemNumber) {
        this.macroItemNumber = macroItemNumber;
        propertyChanged("macroItemNumber", macroItemNumber);
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
        propertyChanged("fieldsSize", true);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(macroItemNumber)) {
            errors.add("Macro Item Number is required");
        } else if (!TextUtils.isInt(macroItemNumber)) {
            errors.add("Macro Item Number must be an integer");
        }
        if (messageValue.isIdentifierRequired() && StringUtils.isEmpty(identifier)) {
            errors.add("Identifier is required");
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Macro;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableTag.getShortTag(
                        VariableSource.Macro,
                        macroItemNumber,
                        messageValue.name().toLowerCase(),
                        messageValue.isIdentifierRequired() ? identifier : null
                ) :
                null;
    }
}
