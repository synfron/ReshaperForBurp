package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

public abstract class Message {
    @Getter
    private final String messageId;

    protected Message(String messageId) {
        this.messageId = messageId;
    }
    public abstract MessageType getMessageType();
}
