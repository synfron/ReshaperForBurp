package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

public class PromptRequestMessage extends Message {

    @Getter
    private final String description;
    @Getter
    private final String text;

    public PromptRequestMessage(String messageId, String description, String text) {
        super(messageId);
        this.description = description;
        this.text = text;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PromptRequest;
    }
}
