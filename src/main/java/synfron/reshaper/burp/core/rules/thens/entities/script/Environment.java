package synfron.reshaper.burp.core.rules.thens.entities.script;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.*;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Environment {

    private static final ThreadLocal<Map<Integer, Dispatcher.Task>> timers = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Integer> timerCount = ThreadLocal.withInitial(() -> 1);
    private static ScriptableObject sharedScope;

    private static synchronized ScriptableObject getSharedScope(Context context) {
        if (sharedScope == null) {
            try {
                context.setLanguageVersion(Context.VERSION_1_7);
                context.getWrapFactory().setJavaPrimitiveWrap(false);
                sharedScope = context.initSafeStandardObjects();
                ScriptableObject.putProperty(sharedScope, "Reshaper", new ReshaperObj());
                ScriptableObject.putProperty(sharedScope, "console", new ConsoleObj());
                ScriptableObject.putProperty(sharedScope, "XMLHttpRequest", new NativeJavaClass(sharedScope, XmlHttpRequestObj.class));
                ScriptableObject.putProperty(sharedScope, "setTimeout", new NativeJavaMethod(getSetTimeoutMember(), "setTimeout"));
                ScriptableObject.putProperty(sharedScope, "setInterval", new NativeJavaMethod(getSetIntervalMember(), "setInterval"));
                ScriptableObject.putProperty(sharedScope, "clearTimeout", new NativeJavaMethod(getClearTimeoutMember(), "clearTimeout"));
                context.evaluateString(sharedScope, getScript("files/core.js"), "<cmd>", 1, null);
                context.evaluateString(sharedScope, getScript("files/fetch.js"), "<cmd>", 1, null);
                sharedScope.sealObject();
            } catch (NoSuchMethodException e) {
                throw new WrappedException(e);
            }
        }
        return sharedScope;
    }

    public static Scriptable getEventScope(Context context) {
        context.setLanguageVersion(Context.VERSION_1_7);
        context.getWrapFactory().setJavaPrimitiveWrap(false);
        Scriptable scope = context.newObject(getSharedScope(context));
        scope.setPrototype(getSharedScope(context));
        scope.setParentScope(null);
        return scope;
    }

    public static String scriptWithWindow(String script) {
        return "let window = this; " + script;
    }

    public static String getScript(String file) {
        try {
            return IOUtils.toString(
                    Objects.requireNonNull(Environment.class.getClassLoader().getResourceAsStream(file)),
                    Charset.defaultCharset()
            );
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static Method getSetTimeoutMember() throws NoSuchMethodException {
        return Environment.class.getMethod("setTimeout", Function.class, Double.class, Object[].class);
    }

    public static Method getSetIntervalMember() throws NoSuchMethodException {
        return Environment.class.getMethod("setInterval", Function.class, Double.class, Object[].class);
    }

    public static Method getClearTimeoutMember() throws NoSuchMethodException {
        return Environment.class.getMethod("clearTimeout", Integer.class);
    }

    public static int setTimeout(Function func, Double delay, Object... args) {
        int timerId = timerCount.get();
        timerCount.set(timerId + 1);
        Dispatcher.Task task = Dispatcher.getCurrent().execute(context -> {
            func.call(context, func.getParentScope(), null, args);
            timers.get().remove(timerId);
        }, delay.longValue(), TimeUnit.MILLISECONDS);
        timers.get().put(timerId, task);
        return timerId;
    }

    public static int setInterval(Function func, Double delay, Object... args) {
        int timerId = timerCount.get();
        timerCount.set(timerId + 1);
        Dispatcher.Task task = Dispatcher.getCurrent().executeWithRepeat(context -> {
            func.call(context, func.getParentScope(), null, args);
        }, delay.longValue(), TimeUnit.MILLISECONDS);
        timers.get().put(timerId, task);
        return timerId;
    }

    public static void clearTimeout(Integer timerId) {
        Dispatcher.Task task = timers.get().remove(timerId);
        if (task != null) {
            task.cancel();
        }
    }
}
