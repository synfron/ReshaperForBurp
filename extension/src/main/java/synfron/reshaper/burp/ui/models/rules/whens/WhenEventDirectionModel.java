package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenEventDirectionModel extends WhenModel<WhenEventDirectionModel, WhenEventDirection> {

    @Getter
    private HttpDataDirection dataDirection;

    public WhenEventDirectionModel(ProtocolType protocolType, WhenEventDirection when, Boolean isNew) {
        super(protocolType, when, isNew);
        dataDirection = when.getDataDirection();
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
        return super.persist();
    }

    @Override
    protected String getTargetName() {
        return dataDirection.name();
    }

    @Override
    public RuleOperationModelType<WhenEventDirectionModel, WhenEventDirection> getType() {
        return WhenModelType.EventDirection;
    }
}
