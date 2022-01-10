package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.RulesEngine;

public class ThenRunRules extends Then<ThenRunRules> {
    private int cacheVersion;
    private transient Rule ruleCache = null;
    @Getter @Setter
    private boolean runSingle = true;
    @Getter @Setter
    private String ruleName;

    public RuleResponse perform(IEventInfo eventInfo) {
        RulesEngine rulesEngine = BurpExtender.getConnector().getRulesEngine();
        RuleResponse ruleResponse;
        if (runSingle) {
            Rule rule;
            boolean hasError = false;
            try {
                rule = getRule(rulesEngine);
            } catch (Exception e) {
                hasError = true;
                throw e;
            } finally {
                if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, ruleName);
            }
            ruleResponse = rulesEngine.run(eventInfo, rule);
        } else {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, "ALL");
            ruleResponse = rulesEngine.run(eventInfo);
        }
        return ruleResponse;
    }

    private Rule getRule(RulesEngine rulesEngine) {
        if (isRuleCacheExpired(rulesEngine)) {
            ruleCache = rulesEngine.getRulesRegistry().getRules().stream()
                    .filter(rule -> StringUtils.isNotEmpty(rule.getName()) && rule.getName().equals(ruleName))
                    .findFirst()
                    .get();
            cacheVersion = rulesEngine.getRulesRegistry().getVersion();
        }
        return ruleCache;
    }

    private boolean isRuleCacheExpired(RulesEngine rulesEngine) {
        return ruleCache == null || cacheVersion != rulesEngine.getRulesRegistry().getVersion();
    }

    @Override
    public RuleOperationType<ThenRunRules> getType() {
        return ThenType.RunRules;
    }
}
