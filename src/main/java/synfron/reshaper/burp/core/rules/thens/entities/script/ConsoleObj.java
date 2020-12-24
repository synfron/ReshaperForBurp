package synfron.reshaper.burp.core.rules.thens.entities.script;

import synfron.reshaper.burp.core.utils.Log;

public class ConsoleObj {
    public void log(Object... args) {
        Log.get().withMessage("Script Log").withPayload(args.length == 1 ? args[0] : args).log();
    }

    public void error(Object... args) {
        Log.get().withMessage("Script Log").withPayload(args.length == 1 ? args[0] : args).logErr();
    }
}
