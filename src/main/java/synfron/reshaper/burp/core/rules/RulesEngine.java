package synfron.reshaper.burp.core.rules;

import lombok.Getter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.core.utils.Log;

import java.util.List;

public class RulesEngine {

    @Getter
    private final RulesRegistry rulesRegistry = new RulesRegistry();

    public void init() {
        rulesRegistry.loadRules();
    }

    public void save() {
        rulesRegistry.saveRules();
    }

    private boolean match(List<When<?>> whens, EventInfo eventInfo)
    {
        boolean  isMatch = true;
        boolean  first = true;
        for (When<?> when : whens)
        {
            if (!isMatch && !when.isUseOrCondition() && !first)
            {
                break;
            }
            if (when.isUseOrCondition() && !first)
            {
                isMatch |= when.isMatch(eventInfo) == !when.isNegate();
            }
            else
            {
                isMatch &= when.isMatch(eventInfo) == !when.isNegate();
            }
            first = false;
        }
        return isMatch;
    }

    private RuleResponse perform(List<Then<?>> thens, EventInfo eventInfo)
    {
        RuleResponse thenResult = RuleResponse.Continue;
        for (Then<?> then : thens)
        {
            RuleResponse result = then.perform(eventInfo);
            thenResult = thenResult.or(result);
            if (result.hasFlags(RuleResponse.BreakThens) || result.hasFlags(RuleResponse.BreakRules))
            {
                break;
            }
        }
        return thenResult;
    }

    public RuleResponse run(EventInfo eventInfo)
    {
        List<Rule> rules = rulesRegistry.getRules();

        for (Rule rule : rules)
        {
            if (rule.isAutoRun()) {
                RuleResponse thenResult = run(eventInfo, rule);
                if (thenResult.hasFlags(RuleResponse.BreakRules)) {
                    break;
                }
            }
        }
        return RuleResponse.Continue;
    }

    public RuleResponse run(EventInfo eventInfo, Rule rule)
    {
        RuleResponse thenResult = RuleResponse.Continue;
        try
        {
            if (rule.isEnabled() && match(rule.getWhens(), eventInfo))
            {
                thenResult = thenResult.or(perform(rule.getThens(), eventInfo));
            }
        }
        catch (Exception e)
        {
            Log.get().withException(e).withMessage("Failure running rule").withPayload(rule).logErr();
        }
        return thenResult;
    }
}
