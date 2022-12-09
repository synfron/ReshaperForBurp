package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.rules.whens.WhenMessageType;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenMessageTypeModel extends WhenModel<WhenMessageTypeModel, WhenMessageType> {

    @Getter
    private WebSocketMessageType messageType;

    public WhenMessageTypeModel(ProtocolType protocolType, WhenMessageType when, Boolean isNew) {
        super(protocolType, when, isNew);
        messageType = when.getMessageType();
    }

    public void setMessageType(WebSocketMessageType messageType) {
        this.messageType = messageType;
        propertyChanged("messageType", messageType);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setMessageType(messageType);
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
    protected String getTargetName() {
        return messageType.name();
    }

    @Override
    public RuleOperationModelType<WhenMessageTypeModel, WhenMessageType> getType() {
        return WhenModelType.MessageType;
    }
}
