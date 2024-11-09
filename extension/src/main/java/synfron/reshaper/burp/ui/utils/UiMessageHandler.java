package synfron.reshaper.burp.ui.utils;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.MessageEvent;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.message.PromptRequestMessage;
import synfron.reshaper.burp.core.events.message.PromptResponseMessage;
import synfron.reshaper.burp.ui.components.TextPromptComponent;
import synfron.reshaper.burp.ui.components.workspaces.WorkspaceComponent;
import synfron.reshaper.burp.ui.models.TextPromptModel;

import java.awt.*;

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

                TextPromptModel model = new TextPromptModel(message.getDescription(), message.getText());

                IEventListener<PropertyChangedArgs> modelPropertyChanged = args -> {
                    if (args.getName().equals("dismissed") && model.isDismissed() && !model.isInvalidated()) {
                        messageEvent.invoke(new MessageArgs(this, new PromptResponseMessage(
                                message.getMessageId(),
                                model.getText()
                        )));
                    }
                };
                model.withListener(modelPropertyChanged);

                ModalPrompter.open(model, new ModalPrompter.FormPromptArgs<>(
                        "Prompt",
                        model,
                        new TextPromptComponent(model)
                )
                        .resizable(true)
                        .size(new Dimension(360, 200))
                        .locationRelativeTo(workspaceComponent)
                        .registrationId(message.getMessageId())
                        .dataBag(modelPropertyChanged)
                );
            }
            case PromptCancel -> ModalPrompter.dismiss(messageArgs.getData().getMessageId());
        }
    }
}
