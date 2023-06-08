package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenDrop;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenDropModel extends ThenModel<ThenDropModel, ThenDrop> {

    @Getter
    private boolean dropMessage;

    public ThenDropModel(ProtocolType protocolType, ThenDrop then, Boolean isNew) {
        super(protocolType, then, isNew);
        dropMessage = then.isDropMessage();
    }

    public void setDropMessage(boolean dropMessage) {
        this.dropMessage = dropMessage;
        propertyChanged("dropMessage", dropMessage);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDropMessage(dropMessage);
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
        return isDropMessage() ? "Yes" : "No";
    }

    @Override
    public RuleOperationModelType<ThenDropModel, ThenDrop> getType() {
        return ThenModelType.Drop;
    }
}
