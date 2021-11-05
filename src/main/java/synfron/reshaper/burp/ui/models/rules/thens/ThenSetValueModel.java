package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenSetValue;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenSetValueModel extends ThenSetModel<ThenSetValueModel, ThenSetValue> {

    @Getter
    private MessageValue destinationMessageValue;
    @Getter
    private String destinationIdentifier = "";

    public ThenSetValueModel(ThenSetValue then, Boolean isNew) {
        super(then, isNew);
        destinationMessageValue = then.getDestinationMessageValue();
        destinationIdentifier = VariableString.getFormattedString(then.getDestinationIdentifier(), destinationIdentifier);
    }

    public void setDestinationMessageValue(MessageValue destinationMessageValue) {
        this.destinationMessageValue = destinationMessageValue;
        propertyChanged("destinationMessageValue", destinationMessageValue);
    }

    public void setDestinationIdentifier(String destinationIdentifier) {
        this.destinationIdentifier = destinationIdentifier;
        propertyChanged("destinationIdentifier", destinationIdentifier);
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
