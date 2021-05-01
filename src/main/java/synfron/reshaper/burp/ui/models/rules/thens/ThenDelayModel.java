package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenDelay;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenDelayModel extends ThenModel<ThenDelayModel, ThenDelay> {

    @Getter
    private String delay;

    public ThenDelayModel(ThenDelay then, Boolean isNew) {
        super(then, isNew);
        delay = VariableString.getFormattedString(then.getDelay(), delay);
    }

    public void setDelay(String delay) {
        this.delay = delay;
        propertyChanged("delay", delay);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(delay)) {
            errors.add("Delay is required");
        } else if (!VariableString.isPotentialInt(delay)) {
            errors.add("Delay must be an integer");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDelay(VariableString.getAsVariableString(delay));
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
    public RuleOperationModelType<ThenDelayModel, ThenDelay> getType() {
        return ThenModelType.Delay;
    }
}
