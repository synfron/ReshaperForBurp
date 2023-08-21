package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenParseHttpMessage;
import synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage.MessageValueGetter;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.parsehttpmessage.MessageValueGetterModel;

import java.util.List;
import java.util.stream.Collectors;

public class ThenParseHttpMessageModel extends ThenModel<ThenParseHttpMessageModel, ThenParseHttpMessage> implements IVariableCreator {

    @Getter
    private HttpDataDirection dataDirection;
    @Getter
    private String httpMessage;
    @Getter
    private final List<MessageValueGetterModel> messageValueGetters;

    private final IEventListener<PropertyChangedArgs> messageValueGetterChangedListener = this::onMessageValueGetterChanged;

    public ThenParseHttpMessageModel(ProtocolType protocolType, ThenParseHttpMessage then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.dataDirection = then.getDataDirection();
        this.httpMessage = VariableString.toString(then.getHttpMessage(), httpMessage);
        this.messageValueGetters = then.getMessageValueGetters().stream()
                .map(messageValueGetter -> new MessageValueGetterModel(messageValueGetter).withListener(messageValueGetterChangedListener))
                .collect(Collectors.toList());
        VariableCreatorRegistry.register(this);
    }

    public MessageValueGetterModel addMessageValueGetter() {
        MessageValueGetterModel messageValueGetterModel = new MessageValueGetterModel(new MessageValueGetter(dataDirection))
                .withListener(messageValueGetterChangedListener);
        messageValueGetters.add(messageValueGetterModel);
        propertyChanged("messageValueGetters", messageValueGetters);
        return messageValueGetterModel;
    }

    public int removeMessageValueGetter(MessageValueGetterModel messageValueGetterModel) {
        int index = messageValueGetters.indexOf(messageValueGetterModel);
        messageValueGetters.remove(index);
        propertyChanged("messageValueGetters", messageValueGetters);
        return index;
    }

    public void setDataDirection(HttpDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    private void onMessageValueGetterChanged(PropertyChangedArgs args) {
        propertyChanged("messageValueGetters", messageValueGetters);
    }

    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
        propertyChanged("httpMessage", httpMessage);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        messageValueGetters.forEach(messageValueGetter -> errors.addAll(messageValueGetter.validate()));
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        ruleOperation.setHttpMessage(VariableString.getAsVariableString(httpMessage));

        messageValueGetters.forEach(MessageValueGetterModel::persist);
        ruleOperation.getMessageValueGetters().clear();
        ruleOperation.getMessageValueGetters().addAll(messageValueGetters.stream()
                .map(MessageValueGetterModel::getMessageValueGetter)
                .collect(Collectors.toList())
        );

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
    protected String getTargetName() {
        return dataDirection.name();
    }

    @Override
    public RuleOperationModelType<ThenParseHttpMessageModel, ThenParseHttpMessage> getType() {
        return ThenModelType.ParseHttpMessage;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return messageValueGetters.stream()
                .filter(getter -> StringUtils.isNotEmpty(getter.getDestinationVariableName()))
                .map(getter -> new VariableSourceEntry(getter.getDestinationVariableSource(), getter.getDestinationVariableName()))
                .collect(Collectors.toList());
    }
}
