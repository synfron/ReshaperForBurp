package synfron.reshaper.burp.core.events.message;

import lombok.Getter;

public enum MessageType {
    PromptRequest(PromptRequestMessage.class),
    PromptResponse(PromptResponseMessage.class),
    PromptCancel(PromptCancelMessage.class);

    @Getter
    private final Class<? extends Message> dataClass;

    MessageType(Class<? extends Message> dataClass) {
        this.dataClass = dataClass;
    }
}
