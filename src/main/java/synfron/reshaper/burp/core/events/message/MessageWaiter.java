package synfron.reshaper.burp.core.events.message;

import synfron.reshaper.burp.core.events.Event;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.MessageEvent;

import java.util.concurrent.*;
import java.util.function.Predicate;

public class MessageWaiter<T extends Message> {
    private final Event<MessageArgs> messageEvent;
    private final MessageType messageType;
    private final Predicate<T> isMatch;
    private T message;
    private final IEventListener<MessageArgs> messageListener = this::onMessage;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MessageWaiter(MessageEvent messageEvent, MessageType messageType, Predicate<T> isMatch) {
        this.messageEvent = messageEvent;
        this.messageType = messageType;
        this.isMatch = isMatch;

        messageEvent.add(messageListener);
    }

    private void onMessage(MessageArgs messageArgs) {
        if (messageType == messageArgs.getData().getMessageType() && isMatch.test((T)messageArgs.getData())) {
            message = (T)messageArgs.getData();
            executor.shutdown();
        }
    }

    public boolean waitForMessage(long timeout, TimeUnit timeUnit) throws InterruptedException {
        executor.submit(() -> {
           if (message != null) {
               executor.shutdown();
           }
        });
        if (!executor.awaitTermination(timeout, timeUnit)) {
            executor.shutdownNow();
        }
        return hasMessage();
    }

    public boolean hasMessage() {
        return message != null;
    }

    public T getMessage() {
        return message;
    }

    public void cancel() {
        messageEvent.remove(messageListener);
    }
}
