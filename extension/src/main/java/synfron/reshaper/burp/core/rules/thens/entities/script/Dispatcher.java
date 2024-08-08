package synfron.reshaper.burp.core.rules.thens.entities.script;

import lombok.Getter;
import lombok.Setter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Dispatcher {
    private static final ThreadLocal<Dispatcher> current = new ThreadLocal<>();
    private final AtomicInteger numTasks = new AtomicInteger(0);
    private ScheduledExecutorService executor;
    @Getter
    private Context context;
    private boolean contextExited = false;
    private Exception firstException;
    private Map<String, Object> dataBag;
    @Getter @Setter
    private int maxExecutionSeconds = 10;
    @Getter
    private boolean timeoutReach = false;

    public Map<String, Object> getDataBag() {
        if (dataBag == null) {
            dataBag = new HashMap<>();
        }
        return dataBag;
    }

    public static Dispatcher getCurrent() {
        return current.get();
    }

    private void setCurrent() {
        current.set(this);
    }

    public EventInfo getEventInfo() {
        return (EventInfo) dataBag.get("eventInfo");
    }

    private Runnable getRunner(Consumer<Context> consumer, boolean shutdownOnException, boolean close) {
        numTasks.incrementAndGet();
        return () -> {
            setCurrent();
            try {
                consumer.accept(getOrCreateContext());
            } catch (Exception e) {
                if (shutdownOnException) {
                    setFirstException(e);
                    executor.shutdownNow();
                } else {
                    Log.get(getEventInfo().getWorkspace()).withMessage("Script execution error")
                            .withException(e)
                            .withPayload(e instanceof RhinoException ? ((RhinoException)e).getScriptStackTrace() : null)
                            .logErr();
                }
                throw e;
            } finally {
                if (close) {
                    registerClose();
                }
            }
        };
    }

    private void setFirstException(Exception e) {
        if (firstException == null) {
            firstException = e;
        }
    }

    private void registerClose() {
        if (numTasks.decrementAndGet() == 0) {
            executor.shutdown();
            exitContext();
        }
    }

    public Task execute(Consumer<Context> consumer) {
        return new Task(executor.submit(getRunner(consumer, false, true)));
    }

    public Task execute(Consumer<Context> consumer, long delay, TimeUnit timeUnit) {
        return new Task(executor.schedule(getRunner(consumer, false, true), delay, timeUnit));
    }

    public Task executeWithRepeat(Consumer<Context> consumer, long delay, TimeUnit timeUnit) {
        return new Task(executor.scheduleAtFixedRate(getRunner(consumer, false, false), delay, delay, timeUnit));
    }

    public void start(Consumer<Context> consumer) {
        try {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.submit(getRunner(consumer, true, true));
            if (!executor.awaitTermination(getMaxExecutionSeconds(), TimeUnit.SECONDS)) {
                executor.shutdownNow();
                timeoutReach = true;
                throw new RuntimeException("Script execution timed out");
            }
            if (firstException != null) {
                if (firstException instanceof RuntimeException) {
                    throw (RuntimeException)firstException;
                } else {
                    throw new WrappedException(firstException);
                }
            }
        } catch (InterruptedException e) {
            Log.get(getEventInfo().getWorkspace()).withMessage("Unexpected script termination").withException(e).logErr();
        }
    }

    private Context getOrCreateContext() {
        if (this.context == null) {
            this.context = Context.enter();
        }
        return this.context;
    }

    private void exitContext() {
        if (!contextExited) {
            contextExited = true;
            Context.exit();
        }
    }

    public class Task {
        private final Future<?> future;

        private Task(Future<?> future) {
            this.future = future;
        }

        public void cancel() {
            future.cancel(true);
            registerClose();
        }
    }
}
