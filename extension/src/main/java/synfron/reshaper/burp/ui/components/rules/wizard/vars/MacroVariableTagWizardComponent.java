package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.MacroVariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class MacroVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final MacroVariableTagWizardModel model;
    private final ProtocolType protocolType;
    private JTextField identifier;
    private JComboBox<MessageValue> messageValue;
    private JTextField macroItemNumber;

    public MacroVariableTagWizardComponent(MacroVariableTagWizardModel model, ProtocolType protocolType) {
        this.model = model;
        this.protocolType = protocolType;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        macroItemNumber = createTextField(false);
        messageValue = createComboBox(Arrays.stream(MessageValue.values())
                .filter(value -> value.isGettable(protocolType))
                .toArray(MessageValue[]::new));
        identifier = createTextField(false);

        macroItemNumber.setText(model.getMacroItemNumber());
        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        macroItemNumber.getDocument().addDocumentListener(new DocumentActionListener(this::onMacroItemNumberChanged));
        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        add(getLabeledField("Macro Item Number *", macroItemNumber), "wrap");
        add(getLabeledField("Message Value", messageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", identifier),
                messageValue,
                () -> ((MessageValue)messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
    }

    private void onMacroItemNumberChanged(ActionEvent actionEvent) {
        model.setMacroItemNumber(macroItemNumber.getText());
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue)messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
