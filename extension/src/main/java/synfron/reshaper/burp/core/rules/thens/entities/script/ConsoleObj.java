package synfron.reshaper.burp.core.rules.thens.entities.script;

import org.mozilla.javascript.ScriptableObject;
import synfron.reshaper.burp.core.utils.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsoleObj {
    public void log(Object... args) {
        List<Object> values = getConsoleWritable(args);
        Log.get().withMessage("Script Log").withPayload(values.size() == 1 ? values.getFirst() : values).log();
    }

    public void error(Object... args) {
        List<Object> values = getConsoleWritable(args);
        Log.get().withMessage("Script Log").withPayload(values.size() == 1 ? values.getFirst() : values).logErr();
    }

    private List<Object> getConsoleWritable(Object[] values) {
        return Arrays.stream(values)
                .map(value -> value instanceof ScriptableObject ? Objects.toString(value) : value)
                .collect(Collectors.toList());
    }
}
