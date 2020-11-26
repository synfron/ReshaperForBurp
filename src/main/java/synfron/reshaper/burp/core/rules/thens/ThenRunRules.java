package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.*;

public class ThenRunRules extends Then<ThenRunRules> {
    private int cacheVersion;
    private transient Rule ruleCache = null;
    @Getter @Setter
    private boolean runSingle = true;
    @Getter @Setter
    private String ruleName;

    public RuleResponse perform(EventInfo eventInfo) {
        RulesEngine rulesEngine = BurpExtender.getConnector().getRulesEngine();
        return runSingle ?
                rulesEngine.run(eventInfo, getRule(rulesEngine)) :
                rulesEngine.run(eventInfo);
    }

    private Rule getRule(RulesEngine rulesEngine) {
        if (isRuleCacheExpired(rulesEngine)) {
            ruleCache = rulesEngine.getRulesRegistry().getRules().stream()
                    .filter(rule -> rule.getName().equals(ruleName))
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
