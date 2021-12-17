package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

public class PromptRequestMessage extends Message {

    @Getter
    private final String description;

    public PromptRequestMessage(String messageId, String description) {
        super(messageId);
        this.description = description;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PromptRequest;
    }
}
