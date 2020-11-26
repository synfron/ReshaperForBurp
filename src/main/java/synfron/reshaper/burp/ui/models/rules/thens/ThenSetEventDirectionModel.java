package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSetEventDirection;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenSetEventDirectionModel extends ThenModel<ThenSetEventDirectionModel, ThenSetEventDirection> {

    @Getter
    private DataDirection dataDirection;

    public ThenSetEventDirectionModel(ThenSetEventDirection then, Boolean isNew) {
        super(then, isNew);
        dataDirection = then.getDataDirection();
    }

    public void setSetDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        propertyChanged("dataDirection", dataDirection);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDataDirection(dataDirection);
        setSaved(true);
        return true;
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
    public RuleOperationModelType<ThenSetEventDirectionModel, ThenSetEventDirection> getType() {
        return ThenModelType.SetEventDirection;
    }
}
