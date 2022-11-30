package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteValue;
import synfron.reshaper.burp.core.utils.DeleteItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Arrays;
import java.util.List;

public class ThenDeleteValueModel extends ThenModel<ThenDeleteValueModel, ThenDeleteValue> {

    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier;
    @Getter
    private DeleteItemPlacement identifierPlacement;

    public ThenDeleteValueModel(ProtocolType protocolType, ThenDeleteValue then, Boolean isNew) {
        super(protocolType, then, isNew);
        messageValue = then.getMessageValue() != null ? then.getMessageValue() : Arrays.stream(MessageValue.values()).filter(value -> value.isDeletable(protocolType)).findFirst().orElse(null);
        identifier = VariableString.toString(then.getIdentifier(), identifier);
        identifierPlacement = then.getIdentifierPlacement();
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    public void setIdentifierPlacement(DeleteItemPlacement identifierPlacement) {
        this.identifierPlacement = identifierPlacement;
        propertyChanged("identifierPlacement", identifierPlacement);
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
        ruleOperation.setIdentifierPlacement(identifierPlacement);
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
