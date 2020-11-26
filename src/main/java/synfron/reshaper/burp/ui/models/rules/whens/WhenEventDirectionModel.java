package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenEventDirectionModel extends WhenModel<WhenEventDirectionModel, WhenEventDirection> {

    @Getter
    private DataDirection dataDirection;

    public WhenEventDirectionModel(WhenEventDirection when, Boolean isNew) {
        super(when, isNew);
        dataDirection = when.getDataDirection();
    }

    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        return super.persist();
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setSaved(true);
        return true;
    }

    @Override
    public RuleOperationModelType<WhenEventDirectionModel, WhenEventDirection> getType() {
        return WhenModelType.EventDirection;
    }
}
