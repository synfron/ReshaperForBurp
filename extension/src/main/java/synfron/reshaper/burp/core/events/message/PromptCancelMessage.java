package synfron.reshaper.burp.core.events.message;

public class PromptCancelMessage extends Message {

    public PromptCancelMessage(String messageId) {
        super(messageId);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PromptCancel;
    }
}
