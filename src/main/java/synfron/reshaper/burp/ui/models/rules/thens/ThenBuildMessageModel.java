package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.thens.ThenBuildMessage;
import synfron.reshaper.burp.core.rules.thens.entities.buildmessage.MessageSetter;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.thens.buildmessage.MessageSetterModel;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ThenBuildMessageModel<P extends ThenBuildMessageModel<P, T>, T extends ThenBuildMessage<T>> extends ThenModel<P, T> {

    @Getter
    private String starterMessage;
    @Getter
    private final List<MessageSetterModel> messageSetters;
    @Getter
    private VariableSource variableSource;
    @Getter
    private String variableName;

    private final IEventListener<PropertyChangedArgs> messageSetterChangedListener = this::onMessageSetterChanged;

    public ThenBuildMessageModel(T then, Boolean isNew) {
        super(then, isNew);
        this.starterMessage = VariableString.getFormattedString(then.getStarterMessage(), starterMessage);
        this.messageSetters = then.getMessageSetters().stream()
                .map(messageSetter -> new MessageSetterModel(messageSetter).withListener(messageSetterChangedListener))
                .collect(Collectors.toList());
        this.variableSource = then.getVariableSource();
        this.variableName = VariableString.getFormattedString(then.getVariableName(), variableName);
    }

    public MessageSetterModel addMessageSetter() {
        MessageSetterModel messageSetterModel = new MessageSetterModel(new MessageSetter()).withListener(messageSetterChangedListener);
        messageSetters.add(messageSetterModel);
        return messageSetterModel;
    }

    public int removeMessageSetter(MessageSetterModel messageSetterModel) {
        int index = messageSetters.indexOf(messageSetterModel);
        messageSetters.remove(index);
        return index;
    }

    private void onMessageSetterChanged(PropertyChangedArgs args) {
        propertyChanged("messageSetters", messageSetters);
    }

    public void setStarterMessage(String starterMessage) {
        this.starterMessage = starterMessage;
        propertyChanged("starterMessage", starterMessage);
    }

    public void setVariableSource(VariableSource variableSource) {
        this.variableSource = variableSource;
        propertyChanged("variableSource", variableSource);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        propertyChanged("variableName", variableName);
    }

    public void markChanged() {
        setValidated(false);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(variableName)) {
            errors.add("Variable Name is required");
        } else if (!VariableString.isValidVariableName(variableName)) {
            errors.add("Variable Name is invalid");
        }
        messageSetters.forEach(messageSetter -> errors.addAll(messageSetter.validate()));
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setStarterMessage(VariableString.getAsVariableString(starterMessage));

        messageSetters.forEach(MessageSetterModel::persist);
        ruleOperation.getMessageSetters().clear();
        ruleOperation.getMessageSetters().addAll(messageSetters.stream()
                .map(MessageSetterModel::getMessageSetter)
                .collect(Collectors.toList())
        );

        ruleOperation.setVariableSource(variableSource);
        ruleOperation.setVariableName(VariableString.getAsVariableString(variableName));
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
}
