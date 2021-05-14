package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.script.Dispatcher;
import synfron.reshaper.burp.core.rules.thens.entities.script.Environment;


public class ThenRunScript extends Then<ThenRunScript> {
    @Getter @Setter
    private String script;
    @Getter @Setter
    private int maxExecutionSeconds = 10;

    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
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
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, hasError, script);
        }
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenRunScript> getType() {
        return ThenType.RunScript;
    }
}

