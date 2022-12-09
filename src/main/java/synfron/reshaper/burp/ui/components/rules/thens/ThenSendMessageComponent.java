package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSendMessage;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendMessageModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSendMessageComponent extends ThenComponent<ThenSendMessageModel, ThenSendMessage> {

    private JComboBox<WebSocketDataDirection> dataDirection;
    private JTextField message;

    public ThenSendMessageComponent(ProtocolType protocolType, ThenSendMessageModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        dataDirection = createComboBox(WebSocketDataDirection.values());
        message = createTextField(true);

        dataDirection.setSelectedItem(model.getDataDirection());
        message.setText(model.getMessage());

        dataDirection.addActionListener(this::onSetEventDirectionChanged);
        message.getDocument().addDocumentListener(new DocumentActionListener(this::onMessageChanged));

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        mainContainer.add(getLabeledField("Message", message), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onSetEventDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((WebSocketDataDirection) dataDirection.getSelectedItem());
    }

    private void onMessageChanged(ActionEvent actionEvent) {
        model.setMessage(message.getText());
    }
}
