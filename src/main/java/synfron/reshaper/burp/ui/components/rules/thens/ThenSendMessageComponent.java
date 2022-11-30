package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSendMessage;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendMessageModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSendMessageComponent extends ThenComponent<ThenSendMessageModel, ThenSendMessage> {
    
    private JTextField message;

    public ThenSendMessageComponent(ProtocolType protocolType, ThenSendMessageModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        message = createTextField(true);

        message.setText(model.getMessage());

        message.getDocument().addDocumentListener(new DocumentActionListener(this::onMessageChanged));

        mainContainer.add(getLabeledField("Message", message), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onMessageChanged(ActionEvent actionEvent) {
        model.setMessage(message.getText());
    }
}
