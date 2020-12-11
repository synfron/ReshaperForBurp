package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.whens.WhenHasEntity;
import synfron.reshaper.burp.ui.models.rules.whens.WhenHasEntityModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenHasEntityComponent extends WhenComponent<WhenHasEntityModel, WhenHasEntity> {
    private JComboBox<MessageValue> messageValue;
    private JTextField identifier;

    public WhenHasEntityComponent(WhenHasEntityModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        identifier = new JTextField();
        messageValue = new JComboBox<>(MessageValue.values());
        JButton save = new JButton("Save");

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        save.addActionListener(this::onSave);

        mainContainer.add(getLabeledField("Message Value", messageValue), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Identifier", identifier),
                messageValue,
                () -> MessageValueHandler.hasIdentifier((MessageValue)messageValue.getSelectedItem())
        ), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(save));
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue)messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }
}
