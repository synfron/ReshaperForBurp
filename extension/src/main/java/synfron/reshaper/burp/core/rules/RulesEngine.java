package synfron.reshaper.burp.core.rules;

import lombok.Getter;
import org.mozilla.javascript.RhinoException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.thens.IThenGroup;
import synfron.reshaper.burp.core.rules.whens.IWhenGroup;
import synfron.reshaper.burp.core.utils.Log;

public class RulesEngine implements IWhenGroup, IThenGroup {

    @Getter
    private final RulesRegistry rulesRegistry = new RulesRegistry();

    public RuleResponse run(EventInfo eventInfo)
    {
        Rule[] rules = rulesRegistry.getRules();
        try {
            for (Rule rule : rules) {
                if (rule.isAutoRun()) {
                    RuleResponse thenResult = run(eventInfo, rule);
                    if (thenResult.hasFlags(RuleResponse.BreakRules)) {
                        break;
                    }
                }
            }
        } finally {
            if (eventInfo.getDiagnostics().hasLogs()) eventInfo.getDiagnostics().logStart(eventInfo);
            if (eventInfo.getDiagnostics().hasLogs()) eventInfo.getDiagnostics().logEnd(eventInfo);
        }
        return RuleResponse.Continue;
    }

    public RuleResponse run(EventInfo eventInfo, Rule rule)
    {
        RuleResponse thenResult = RuleResponse.Continue;
        boolean ruleDiagnosticsEnabled = eventInfo.getDiagnostics().isRuleEnabled();
        boolean currentRuleDiagnosticsEnabled = rule.isDiagnosticsEnabled();
        if (!ruleDiagnosticsEnabled && currentRuleDiagnosticsEnabled) {
            eventInfo.getDiagnostics().setRuleEnabled(true);
        }
        if (rule.isEnabled() && eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logStart(rule);
        try
        {
            if (rule.isEnabled() && isMatch(rule.getWhens(), eventInfo))
            {
                thenResult = thenResult.or(perform(rule.getThens(), eventInfo));
            }
        } catch (RhinoException e) {
            Log.get().withMessage("Failure running rule").withException(e).withPayload(e.getScriptStackTrace()).logErr();
        } catch (Exception e) {
            Log.get().withException(e).withMessage("Failure running rule").withPayload(rule).logErr();
        } finally {
            if (rule.isEnabled() && eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logEnd(rule);
        }
        if (!ruleDiagnosticsEnabled && currentRuleDiagnosticsEnabled) {
            eventInfo.getDiagnostics().setRuleEnabled(false);
        }
        return thenResult;
    }
}
