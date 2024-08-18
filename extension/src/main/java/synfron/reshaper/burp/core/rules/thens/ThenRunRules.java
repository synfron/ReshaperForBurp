package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.rules.*;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class ThenRunRules extends Then<ThenRunRules> implements IHttpRuleOperation, IWebSocketRuleOperation {

    private transient int cacheVersion;
    private transient Rule ruleCache = null;
    @Getter @Setter
    private boolean runSingle = true;
    @Getter @Setter
    private String ruleName;

    private RulesEngine getRulesEngine(EventInfo eventInfo) {
        return (eventInfo instanceof HttpEventInfo) ?
                eventInfo.getWorkspace().getHttpConnector().getRulesEngine() :
                eventInfo.getWorkspace().getWebSocketConnector().getRulesEngine();
    }

    public RuleResponse perform(EventInfo eventInfo) {
        RulesEngine rulesEngine = getRulesEngine(eventInfo);
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
            ruleCache = Stream.of(rulesEngine.getRulesRegistry().getRules())
                    .filter(rule -> StringUtils.isNotEmpty(rule.getName()) && rule.getName().equals(ruleName))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(String.format("Rule '%s' not found", ruleName)));
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
