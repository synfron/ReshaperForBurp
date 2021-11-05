package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildHttpMessage;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage.MessageValueSetterModel;

import java.util.List;
import java.util.stream.Collectors;

public class ThenBuildHttpMessageModel extends ThenModel<ThenBuildHttpMessageModel, ThenBuildHttpMessage> {

    @Getter
    private DataDirection dataDirection;
    @Getter
    private String starterHttpMessage;
    @Getter
    private final List<MessageValueSetterModel> messageValueSetters;
    @Getter
    private VariableSource destinationVariableSource;
    @Getter
    private String DestinationVariableName;

    private final IEventListener<PropertyChangedArgs> messageValueSetterChangedListener = this::onMessageValueSetterChanged;

    public ThenBuildHttpMessageModel(ThenBuildHttpMessage then, Boolean isNew) {
        super(then, isNew);
        this.dataDirection = then.getDataDirection();
        this.starterHttpMessage = VariableString.getFormattedString(then.getStarterHttpMessage(), starterHttpMessage);
        this.messageValueSetters = then.getMessageValueSetters().stream()
                .map(messageValueSetter -> new MessageValueSetterModel(messageValueSetter).withListener(messageValueSetterChangedListener))
                .collect(Collectors.toList());
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.DestinationVariableName = VariableString.getFormattedString(then.getDestinationVariableName(), DestinationVariableName);
    }

    public MessageValueSetterModel addMessageValueSetter() {
        MessageValueSetterModel messageValueSetterModel = new MessageValueSetterModel(new MessageValueSetter(dataDirection))
                .withListener(messageValueSetterChangedListener);
        messageValueSetters.add(messageValueSetterModel);
        propertyChanged("messageValueSetters", messageValueSetters);
        return messageValueSetterModel;
    }

    public int removeMessageValueSetter(MessageValueSetterModel messageValueSetterModel) {
        int index = messageValueSetters.indexOf(messageValueSetterModel);
        messageValueSetters.remove(index);
        propertyChanged("messageValueSetters", messageValueSetters);
        return index;
    }

    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    private void onMessageValueSetterChanged(PropertyChangedArgs args) {
        propertyChanged("messageValueSetters", messageValueSetters);
    }

    public void setStarterHttpMessage(String starterHttpMessage) {
        this.starterHttpMessage = starterHttpMessage;
        propertyChanged("starterHttpMessage", starterHttpMessage);
    }

    public void setDestinationVariableSource(VariableSource destinationVariableSource) {
        this.destinationVariableSource = destinationVariableSource;
        propertyChanged("destinationVariableSource", destinationVariableSource);
    }

    public void setDestinationVariableName(String destinationVariableName) {
        this.DestinationVariableName = destinationVariableName;
        propertyChanged("destinationVariableName", destinationVariableName);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(DestinationVariableName)) {
            errors.add("Variable Name is required");
        } else if (!VariableString.isValidVariableName(DestinationVariableName)) {
            errors.add("Variable Name is invalid");
        }
        messageValueSetters.forEach(messageValueSetter -> errors.addAll(messageValueSetter.validate()));
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        ruleOperation.setStarterHttpMessage(VariableString.getAsVariableString(starterHttpMessage));

        messageValueSetters.forEach(MessageValueSetterModel::persist);
        ruleOperation.getMessageValueSetters().clear();
        ruleOperation.getMessageValueSetters().addAll(messageValueSetters.stream()
                .map(MessageValueSetterModel::getMessageValueSetter)
                .collect(Collectors.toList())
        );

        ruleOperation.setDestinationVariableSource(destinationVariableSource);
        ruleOperation.setDestinationVariableName(VariableString.getAsVariableString(DestinationVariableName));
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
    public RuleOperationModelType<ThenBuildHttpMessageModel, ThenBuildHttpMessage> getType() {
        return ThenModelType.BuildHttpMessage;
    }
}
