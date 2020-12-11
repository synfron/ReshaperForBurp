package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.ConsoleObj;
import synfron.reshaper.burp.core.rules.thens.entities.ReshaperObj;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;


public class ThenRunScript extends Then<ThenRunScript> {
    private static ScriptableObject sharedScope;
    @Getter @Setter
    private String script;



    public RuleResponse perform(EventInfo eventInfo) {
        try {
            Context context = Context.enter();
            Scriptable scope = context.newObject(getSharedScope());
            scope.setPrototype(getSharedScope());
            scope.setParentScope(null);
            ScriptableObject.putProperty(scope, "Reshaper", new ReshaperObj(eventInfo));
            ScriptableObject.putProperty(scope, "console", new ConsoleObj());
            context.evaluateString(scope, script, "<cmd>", 1, null);
        } finally {
            Context.exit();
        }
        return RuleResponse.Continue;
    }

    private static synchronized ScriptableObject getSharedScope() {
        if (sharedScope == null) {
            try {
                Context context = Context.enter();
                sharedScope = context.initSafeStandardObjects();
                String coreJs = IOUtils.toString(
                        Objects.requireNonNull(ThenRunScript.class.getClassLoader().getResourceAsStream("files/core.js")),
                        Charset.defaultCharset()
                );
                context.evaluateString(sharedScope, coreJs, "<cmd>", 1, null);
                sharedScope.sealObject();
            } catch (IOException e) {
                throw new WrappedException(e);
            } finally {
                Context.exit();
            }
        }
        return sharedScope;
    }

    @Override
    public RuleOperationType<ThenRunScript> getType() {
        return ThenType.RunScript;
    }
}

