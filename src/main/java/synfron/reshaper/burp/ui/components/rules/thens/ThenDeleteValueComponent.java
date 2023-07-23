package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteValue;
import synfron.reshaper.burp.core.rules.DeleteItemPlacement;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDeleteValueModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class ThenDeleteValueComponent extends ThenComponent<ThenDeleteValueModel, ThenDeleteValue> {
    protected JComboBox<MessageValue> messageValue;
    protected JTextField identifier;
    protected JComboBox<DeleteItemPlacement> identifierPlacement;

    public ThenDeleteValueComponent(ProtocolType protocolType, ThenDeleteValueModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        messageValue = createComboBox(Arrays.stream(MessageValue.values())
                .filter(value -> value.isDeletable(protocolType))
                .toArray(MessageValue[]::new)
        );
        identifier = createTextField(true);
        identifierPlacement = createComboBox(DeleteItemPlacement.values());

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());
        identifierPlacement.setSelectedItem(model.getIdentifierPlacement());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        identifierPlacement.addActionListener(this::onIdentifierPlacementChanged);

        mainContainer.add(getLabeledField("Message Value", messageValue), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier *", identifier),
                messageValue,
                () -> ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier Placement", identifierPlacement),
                messageValue,
                () -> ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue) messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }

    private void onIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setIdentifierPlacement((DeleteItemPlacement) identifierPlacement.getSelectedItem());
    }
}
