package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.MessageVariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class MessageVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final MessageVariableTagWizardModel model;
    private final ProtocolType protocolType;
    private JComboBox<MessageValue> messageValue;
    private JTextField identifier;

    public MessageVariableTagWizardComponent(MessageVariableTagWizardModel model, ProtocolType protocolType) {
        this.model = model;
        this.protocolType = protocolType;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        messageValue = createComboBox(Arrays.stream(MessageValue.values())
                .filter(value -> value.isGettable(protocolType))
                .toArray(MessageValue[]::new));
        identifier = createTextField(true);

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        add(getLabeledField("Message Value", messageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", identifier),
                messageValue,
                () -> ((MessageValue)messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue)messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }
}
