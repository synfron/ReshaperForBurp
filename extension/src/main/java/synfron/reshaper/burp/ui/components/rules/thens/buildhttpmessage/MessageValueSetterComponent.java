package synfron.reshaper.burp.ui.components.rules.thens.buildhttpmessage;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
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
    private final HttpDataDirection dataDirection;
    private final boolean deletable;
    private JComboBox<MessageValue> destinationMessageValue;
    private JTextField destinationIdentifier;
    private JComboBox<SetItemPlacement> destinationIdentifierPlacement;
    private JTextField sourceText;

    public MessageValueSetterComponent(MessageValueSetterModel model, HttpDataDirection dataDirection, boolean deletable) {
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

        sourceText = createTextField(true);
        destinationMessageValue = createComboBox(
                Stream.of(MessageValue.values())
                        .filter(messageValue -> messageValue.getDataDirection() == dataDirection && messageValue.isInnerLevelSettable(ProtocolType.Http))
                        .toArray(MessageValue[]::new)
        );
        destinationIdentifier = createTextField(true);
        destinationIdentifierPlacement = createComboBox(SetItemPlacement.values());

        sourceText.setText(model.getSourceText());
        destinationMessageValue.setSelectedItem(model.getDestinationMessageValue());
        destinationIdentifier.setText(model.getDestinationIdentifier());
        destinationIdentifierPlacement.setSelectedItem(model.getDestinationIdentifierPlacement());

        sourceText.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        destinationMessageValue.addActionListener(this::onMessageValueChanged);
        destinationIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        destinationIdentifierPlacement.addActionListener(this::onDestinationIdentifierPlacementChanged);

        add(getLabeledField("Source Text", sourceText), "wrap");
        add(getLabeledField("Destination Message Value", destinationMessageValue), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Destination Identifier *", destinationIdentifier),
                List.of(destinationMessageValue),
                () -> ((MessageValue) destinationMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Destination Identifier Placement", destinationIdentifierPlacement),
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

    private void onDestinationIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setDestinationIdentifierPlacement((SetItemPlacement) destinationIdentifierPlacement.getSelectedItem());
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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
