package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenRunRules;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRunRulesModel extends ThenModel<ThenRunRulesModel, ThenRunRules> {

    @Getter
    private boolean runSingle;
    @Getter
    private String ruleName;

    public ThenRunRulesModel(ThenRunRules then, Boolean isNew) {
        super(then, isNew);
        runSingle = then.isRunSingle();
        ruleName = then.getRuleName();
    }

    public void setRunSingle(boolean runSingle) {
        this.runSingle = runSingle;
        propertyChanged("runSingle", runSingle);
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
        propertyChanged("ruleName", ruleName);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (runSingle && StringUtils.isEmpty(ruleName)) {
            errors.add("Rule Name is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setRuleName(ruleName);
        ruleOperation.setRunSingle(runSingle);
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
    public RuleOperationModelType<ThenRunRulesModel, ThenRunRules> getType() {
        return ThenModelType.RunRules;
    }
}
