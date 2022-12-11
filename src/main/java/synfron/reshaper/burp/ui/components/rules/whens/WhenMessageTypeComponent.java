package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.rules.whens.WhenMessageType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenMessageTypeModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenMessageTypeComponent extends WhenComponent<WhenMessageTypeModel, WhenMessageType> {
    private JComboBox<WebSocketMessageType> messageType;

    public WhenMessageTypeComponent(ProtocolType protocolType, WhenMessageTypeModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        messageType = createComboBox(WebSocketMessageType.values());

        messageType.setSelectedItem(model.getMessageType());

        messageType.addActionListener(this::onMessageTypeChanged);

        mainContainer.add(getLabeledField("Message Type", messageType), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onMessageTypeChanged(ActionEvent actionEvent) {
        model.setMessageType((WebSocketMessageType)messageType.getSelectedItem());
    }
}
