package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

import java.util.UUID;

public class LogMessage extends Message {

    @Getter
    private final String text;

    public LogMessage(String text) {
        super(UUID.randomUUID().toString());
        this.text = text;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.Log;
    }
}
