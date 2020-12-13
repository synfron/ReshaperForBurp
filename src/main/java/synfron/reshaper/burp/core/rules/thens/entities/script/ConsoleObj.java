package synfron.reshaper.burp.core.rules.thens.entities.script;

import synfron.reshaper.burp.core.utils.Log;

public class ConsoleObj {
    public void log(Object value) {
        Log.get().withMessage("Script Log").withPayload(value).log();
    }

    public void error(Object value) {
        Log.get().withMessage("Script Log").withPayload(value).logErr();
    }
}
