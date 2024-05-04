package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenBuildHttpMessage;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage.MessageValueSetterModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ThenBuildHttpMessageModel extends ThenModel<ThenBuildHttpMessageModel, ThenBuildHttpMessage> implements IVariableCreator {

    @Getter
    private HttpDataDirection dataDirection;
    @Getter
    private String starterHttpMessage;
    @Getter
    private final List<MessageValueSetterModel> messageValueSetters;
    @Getter
    private VariableSource destinationVariableSource;
    @Getter
    private String destinationVariableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;

    private final IEventListener<PropertyChangedArgs> messageValueSetterChangedListener = this::onMessageValueSetterChanged;

    public ThenBuildHttpMessageModel(ProtocolType protocolType, ThenBuildHttpMessage then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.dataDirection = then.getDataDirection();
        this.starterHttpMessage = VariableString.toString(then.getStarterHttpMessage(), starterHttpMessage);
        this.messageValueSetters = then.getMessageValueSetters().stream()
                .map(messageValueSetter -> new MessageValueSetterModel(messageValueSetter).withListener(messageValueSetterChangedListener))
                .collect(Collectors.toList());
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.destinationVariableName = VariableString.toString(then.getDestinationVariableName(), destinationVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
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

    public void setDataDirection(HttpDataDirection dataDirection) {
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
        this.destinationVariableName = destinationVariableName;
        propertyChanged("destinationVariableName", destinationVariableName);
    }

    public void setItemPlacement(SetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(destinationVariableName)) {
            errors.add("Destination Variable Name is required");
        } else if (!VariableString.isValidVariableName(destinationVariableName)) {
            errors.add("Destination Variable Name is invalid");
        }
        if (destinationVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        messageValueSetters.forEach(messageValueSetter -> errors.addAll(messageValueSetter.validate()));
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        ruleOperation.setStarterHttpMessage(VariableString.getAsVariableString(starterHttpMessage));

        messageValueSetters.forEach(MessageValueSetterModel::persist);
        ruleOperation.getMessageValueSetters().clear();
        ruleOperation.getMessageValueSetters().addAll(messageValueSetters.stream()
                .map(MessageValueSetterModel::getMessageValueSetter).toList()
        );

        ruleOperation.setDestinationVariableSource(destinationVariableSource);
        ruleOperation.setDestinationVariableName(VariableString.getAsVariableString(destinationVariableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return dataDirection.name();
    }

    @Override
    public RuleOperationModelType<ThenBuildHttpMessageModel, ThenBuildHttpMessage> getType() {
        return ThenModelType.BuildHttpMessage;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(destinationVariableName) ?
                List.of(new VariableSourceEntry(destinationVariableSource, destinationVariableName)) :
                Collections.emptyList();
    }
}
