package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.script.Dispatcher;
import synfron.reshaper.burp.core.rules.thens.entities.script.Environment;


public class ThenRunScript extends Then<ThenRunScript> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private String script;
    @Getter @Setter
    private int maxExecutionSeconds = 10;

    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        RuleResponse ruleResponse = RuleResponse.Continue;
        try {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxExecutionSeconds(maxExecutionSeconds);
            dispatcher.getDataBag().put("eventInfo", eventInfo);

            dispatcher.start(context -> context.evaluateString(
                    Environment.getEventScope(context),
                    Environment.scriptWithWindow(script),
                    "<cmd>",
                    1,
                    null
            ));

            ruleResponse = (RuleResponse)dispatcher.getDataBag().getOrDefault("ruleResponse", ruleResponse);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, script);
        }
        return ruleResponse;
    }

    @Override
    public RuleOperationType<ThenRunScript> getType() {
        return ThenType.RunScript;
    }
}

