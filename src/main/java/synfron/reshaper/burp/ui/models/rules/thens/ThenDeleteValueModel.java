package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteValue;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenDeleteValueModel extends ThenModel<ThenDeleteValueModel, ThenDeleteValue> {

    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier;

    public ThenDeleteValueModel(ThenDeleteValue then, Boolean isNew) {
        super(then, isNew);
        messageValue = then.getMessageValue();
        identifier = VariableString.getTag(then.getIdentifier(), identifier);
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

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
        ruleOperation.setIdentifier(VariableString.getAsVariableString(identifier));
        ruleOperation.setMessageValue(messageValue);
        setValidated(true);
        return true;
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
    public RuleOperationModelType<ThenDeleteValueModel, ThenDeleteValue> getType() {
        return ThenModelType.DeleteValue;
    }
}
