package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

public class PromptResponseMessage extends Message {

    @Getter
    private final String response;

    public PromptResponseMessage(String messageId, String response) {
        super(messageId);
        this.response = response;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PromptResponse;
    }
}
