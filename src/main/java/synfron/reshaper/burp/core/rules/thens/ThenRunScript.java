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
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenRunScript> getType() {
        return ThenType.RunScript;
    }
}

