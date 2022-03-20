package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.whens.WhenHasEntity;
import synfron.reshaper.burp.ui.models.rules.whens.WhenHasEntityModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
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
        identifier = createTextField(true);
        messageValue = createComboBox(MessageValue.values());

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        mainContainer.add(getLabeledField("Message Value", messageValue), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", identifier),
                messageValue,
                () -> ((MessageValue)messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue)messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }
}
