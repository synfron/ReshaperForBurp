package synfron.reshaper.burp.ui.utils;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.MessageEvent;
import synfron.reshaper.burp.core.events.message.PromptRequestMessage;
import synfron.reshaper.burp.core.events.message.PromptResponseMessage;
import synfron.reshaper.burp.ui.components.workspaces.WorkspaceComponent;

public class UiMessageHandler {
    private final MessageEvent messageEvent;
    private final WorkspaceComponent workspaceComponent;
    private final IEventListener<MessageArgs> messageListener = this::onMessage;

    public UiMessageHandler(MessageEvent messageEvent, WorkspaceComponent workspaceComponent) {
        this.messageEvent = messageEvent;
        this.workspaceComponent = workspaceComponent;
        messageEvent.add(messageListener);
    }

    private void onMessage(MessageArgs messageArgs) {
        switch (messageArgs.getData().getMessageType()) {
            case PromptRequest -> {
                PromptRequestMessage message = (PromptRequestMessage)messageArgs.getData();
                ModalPrompter.createTextAreaDialog(
                        message.getMessageId(),
                        "Prompt",
                        message.getDescription(),
                        message.getText(),
                        value -> messageEvent.invoke(new MessageArgs(this, new PromptResponseMessage(
                                message.getMessageId(),
                                value
                        ))),
                        workspaceComponent
                );
            }
            case PromptCancel -> ModalPrompter.dismiss(messageArgs.getData().getMessageId());
        }
    }
}
