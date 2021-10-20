package synfron.reshaper.burp.ui.components.rules.thens.buildmessage;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.buildmessage.MessageSetterModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Stream;

public class MessageSetterComponent extends JPanel implements IFormComponent {

    private final MessageSetterModel model;
    private final DataDirection dataDirection;
    private final boolean deletable;
    private JComboBox<MessageValue> messageValue;
    private JTextField identifier;
    private JTextField text;

    public MessageSetterComponent(MessageSetterModel model, DataDirection dataDirection, boolean deletable) {
        this.dataDirection = dataDirection;
        this.deletable = deletable;
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        text = new JTextField();
        messageValue = new JComboBox<>(
                Stream.of(MessageValue.values()).filter(messageValue -> messageValue.getDataDirection() == dataDirection).toArray(MessageValue[]::new)
        );
        identifier = new JTextField();

        text.setText(model.getText());
        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        add(getLabeledField("Text", text), "wrap");
        add(getLabeledField("Message Value", messageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier", identifier),
                List.of(messageValue),
                () -> MessageValueHandler.hasIdentifier((MessageValue)messageValue.getSelectedItem())
        ), "wrap");
        if (deletable) {
            JButton delete = new JButton("Delete");
            delete.addActionListener(this::onDelete);
            add(getPaddedButton(delete));
        }
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue) messageValue.getSelectedItem());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onDelete(ActionEvent actionEvent) {
        model.setDeleted(true);
    }

    protected Component getPaddedButton(JButton button) {
        JPanel outerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerContainer.setAlignmentX(LEFT_ALIGNMENT);
        outerContainer.setAlignmentY(TOP_ALIGNMENT);
        outerContainer.add(button);
        return outerContainer;
    }
}
