package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRunRules;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRunRulesModel extends ThenModel<ThenRunRulesModel, ThenRunRules> {

    @Getter
    private boolean runSingle;
    @Getter
    private String ruleName;

    public ThenRunRulesModel(ProtocolType protocolType, ThenRunRules then, Boolean isNew) {
        super(protocolType, then, isNew);
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
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setRuleName(ruleName);
        ruleOperation.setRunSingle(runSingle);
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return runSingle ? abbreviateTargetName(ruleName) : "All";
    }

    @Override
    public RuleOperationModelType<ThenRunRulesModel, ThenRunRules> getType() {
        return ThenModelType.RunRules;
    }
}
