package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSendMessage;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenSendMessageModel extends ThenModel<ThenSendMessageModel, ThenSendMessage> {

    @Getter
    private WebSocketDataDirection dataDirection;

    @Getter
    private String message = "";

    public ThenSendMessageModel(ProtocolType protocolType, ThenSendMessage then, Boolean isNew) {
        super(protocolType, then, isNew);
        dataDirection = then.getDataDirection();
        message = VariableString.toString(then.getMessage(), message);
    }

    public void setDataDirection(WebSocketDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    public void setMessage(String message) {
        this.message = message;
        propertyChanged("message", message);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        ruleOperation.setMessage(VariableString.getAsVariableString(message));
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
        return abbreviateTargetName(message);
    }

    @Override
    public RuleOperationModelType<ThenSendMessageModel, ThenSendMessage> getType() {
        return ThenModelType.SendMessage;
    }
}
