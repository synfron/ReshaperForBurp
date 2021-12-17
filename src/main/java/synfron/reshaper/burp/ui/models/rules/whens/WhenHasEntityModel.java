package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.whens.WhenHasEntity;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class WhenHasEntityModel extends WhenModel<WhenHasEntityModel, WhenHasEntity> {

    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier = "";

    public WhenHasEntityModel(WhenHasEntity when, Boolean isNew) {
        super(when, isNew);
        messageValue = when.getMessageValue();
        identifier = VariableString.getTag(when.getIdentifier(), identifier);
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
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(identifier) && messageValue.isIdentifierRequired()) {
            errors.add("Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setMessageValue(messageValue);
        ruleOperation.setIdentifier(VariableString.getAsVariableString(identifier));
        return super.persist();
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    public RuleOperationModelType<WhenHasEntityModel, WhenHasEntity> getType() {
        return WhenModelType.HasEntity;
    }
}
