package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.rules.thens.ThenSendMessage;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenSendMessageModel extends ThenModel<ThenSendMessageModel, ThenSendMessage> {

    @Getter
    private WebSocketDataDirection dataDirection;

    @Getter
    private WebSocketMessageType messageType;

    @Getter
    private String message = "";

    public ThenSendMessageModel(ProtocolType protocolType, ThenSendMessage then, Boolean isNew) {
        super(protocolType, then, isNew);
        dataDirection = then.getDataDirection();
        messageType = then.getMessageType();
        message = VariableString.toString(then.getMessage(), message);
    }

    public void setDataDirection(WebSocketDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    public void setMessageType(WebSocketMessageType messageType) {
        this.messageType = messageType;
        propertyChanged("messageType", messageType);
    }

    public void setMessage(String message) {
        this.message = message;
        propertyChanged("message", message);
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        ruleOperation.setMessageType(messageType);
        ruleOperation.setMessage(VariableString.getAsVariableString(message));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(message);
    }

    @Override
    public RuleOperationModelType<ThenSendMessageModel, ThenSendMessage> getType() {
        return ThenModelType.SendMessage;
    }
}
