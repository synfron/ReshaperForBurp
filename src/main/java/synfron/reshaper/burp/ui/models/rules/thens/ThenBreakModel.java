package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.ThenBreak;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class ThenBreakModel extends ThenModel<ThenBreakModel, ThenBreak> {

    @Getter
    private RuleResponse breakType;

    public ThenBreakModel(ProtocolType protocolType, ThenBreak then, Boolean isNew) {
        super(protocolType, then, isNew);
        breakType = then.getBreakType();
    }

    public void setBreakType(RuleResponse breakType) {
        this.breakType = breakType;
        propertyChanged("breakType", breakType);
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setBreakType(breakType);
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
        return breakType.getName();
    }

    @Override
    public RuleOperationModelType<ThenBreakModel, ThenBreak> getType() {
        return ThenModelType.Break;
    }
}
