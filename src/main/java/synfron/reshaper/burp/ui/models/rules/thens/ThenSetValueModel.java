package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenSetValue;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Arrays;
import java.util.List;

public class ThenSetValueModel extends ThenSetModel<ThenSetValueModel, ThenSetValue> {

    @Getter
    private MessageValue destinationMessageValue;
    @Getter
    private String destinationIdentifier = "";
    @Getter
    private SetItemPlacement destinationIdentifierPlacement;

    public ThenSetValueModel(ProtocolType protocolType, ThenSetValue then, Boolean isNew) {
        super(protocolType, then, isNew);
        destinationMessageValue = then.getDestinationMessageValue() != null ? then.getDestinationMessageValue() : Arrays.stream(MessageValue.values()).filter(value -> value.isSettable(protocolType)).findFirst().orElse(null);;
        destinationIdentifier = VariableString.toString(then.getDestinationIdentifier(), destinationIdentifier);
        destinationIdentifierPlacement = then.getDestinationIdentifierPlacement();
    }

    public void setDestinationMessageValue(MessageValue destinationMessageValue) {
        this.destinationMessageValue = destinationMessageValue;
        propertyChanged("destinationMessageValue", destinationMessageValue);
    }

    public void setDestinationIdentifier(String destinationIdentifier) {
        this.destinationIdentifier = destinationIdentifier;
        propertyChanged("destinationIdentifier", destinationIdentifier);
    }

    public void setDestinationIdentifierPlacement(SetItemPlacement destinationIdentifierPlacement) {
        this.destinationIdentifierPlacement = destinationIdentifierPlacement;
        propertyChanged("destinationIdentifierPlacement", destinationIdentifierPlacement);
    }


    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(destinationIdentifier) && destinationMessageValue.isIdentifierRequired()) {
            errors.add("Destination Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDestinationMessageValue(destinationMessageValue);
        ruleOperation.setDestinationIdentifier(VariableString.getAsVariableString(destinationIdentifier));
        ruleOperation.setDestinationIdentifierPlacement(destinationIdentifierPlacement);
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
    public RuleOperationModelType<ThenSetValueModel, ThenSetValue> getType() {
        return ThenModelType.SetValue;
    }
}
