package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.ReshaperObj;


public class ThenRunScript extends Then<ThenRunScript> {
    @Getter @Setter
    private String script;

    public RuleResponse perform(EventInfo eventInfo) {
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();
        ScriptableObject.putProperty(scope, "Reshaper", new ReshaperObj(eventInfo));
        context.evaluateString(scope, script, "<cmd>", 1, null);
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenRunScript> getType() {
        return ThenType.RunScript;
    }
}

