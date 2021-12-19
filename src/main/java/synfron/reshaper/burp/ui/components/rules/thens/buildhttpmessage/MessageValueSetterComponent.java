package synfron.reshaper.burp.ui.components.rules.thens.buildhttpmessage;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.buildhttpmessage.MessageValueSetterModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Stream;

public class MessageValueSetterComponent extends JPanel implements IFormComponent {

    private final MessageValueSetterModel model;
    private final DataDirection dataDirection;
    private final boolean deletable;
    private JComboBox<MessageValue> destinationMessageValue;
    private JTextField destinationIdentifier;
    private JTextField sourceText;

    public MessageValueSetterComponent(MessageValueSetterModel model, DataDirection dataDirection, boolean deletable) {
        this.dataDirection = dataDirection;
        this.deletable = deletable;
        setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Setter"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        sourceText = new JTextField();
        destinationMessageValue = new JComboBox<>(
                Stream.of(MessageValue.values())
                        .filter(messageValue -> messageValue.getDataDirection() == dataDirection && !messageValue.isTopLevel())
                        .toArray(MessageValue[]::new)
        );
        destinationIdentifier = new JTextField();

        sourceText.setText(model.getSourceText());
        destinationMessageValue.setSelectedItem(model.getDestinationMessageValue());
        destinationIdentifier.setText(model.getDestinationIdentifier());

        sourceText.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        destinationMessageValue.addActionListener(this::onMessageValueChanged);
        destinationIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        add(getLabeledField("Source Text", sourceText), "wrap");
        add(getLabeledField("Destination Message Value", destinationMessageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Destination Identifier *", destinationIdentifier),
                List.of(destinationMessageValue),
                () -> ((MessageValue) destinationMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        if (deletable) {
            JButton delete = new JButton("Delete");
            delete.addActionListener(this::onDelete);
            add(getPaddedButton(delete));
        }
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setDestinationIdentifier(destinationIdentifier.getText());
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setDestinationMessageValue((MessageValue) destinationMessageValue.getSelectedItem());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setSourceText(sourceText.getText());
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
