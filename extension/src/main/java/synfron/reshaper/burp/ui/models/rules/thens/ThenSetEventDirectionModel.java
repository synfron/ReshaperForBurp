package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSetEventDirection;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenSetEventDirectionModel extends ThenModel<ThenSetEventDirectionModel, ThenSetEventDirection> {

    @Getter
    private HttpDataDirection dataDirection;

    public ThenSetEventDirectionModel(ProtocolType protocolType, ThenSetEventDirection then, Boolean isNew) {
        super(protocolType, then, isNew);
        dataDirection = then.getDataDirection();
    }

    public void setDataDirection(HttpDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return dataDirection.name();
    }

    @Override
    public RuleOperationModelType<ThenSetEventDirectionModel, ThenSetEventDirection> getType() {
        return ThenModelType.SetEventDirection;
    }
}
