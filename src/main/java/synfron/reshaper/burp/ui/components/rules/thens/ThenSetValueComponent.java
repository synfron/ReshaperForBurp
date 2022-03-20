package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenSetValue;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetValueModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSetValueComponent extends ThenSetComponent<ThenSetValueModel, ThenSetValue> {

    private JComboBox<MessageValue> destinationMessageValue;
    private JTextField destinationIdentifier;
    private JComboBox<SetItemPlacement> destinationIdentifierPlacement;

    public ThenSetValueComponent(ThenSetValueModel then) {
        super(then);
    }

    @Override
    protected List<Component> getExtendedComponents() {
        destinationMessageValue = createComboBox(MessageValue.values());
        destinationIdentifier = createTextField(true);
        destinationIdentifierPlacement = createComboBox(SetItemPlacement.values());

        destinationMessageValue.setSelectedItem(model.getDestinationMessageValue());
        destinationIdentifier.setText(model.getDestinationIdentifier());
        destinationIdentifierPlacement.setSelectedItem(model.getDestinationIdentifierPlacement());

        destinationMessageValue.addActionListener(this::onDestinationMessageValueChanged);
        destinationIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationIdentifierChanged));
        destinationIdentifierPlacement.addActionListener(this::onDestinationIdentifierPlacementChanged);

        return List.of(
                getLabeledField("Destination Message Value", destinationMessageValue),
                ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                        getLabeledField("Destination Identifier *", destinationIdentifier),
                        destinationMessageValue,
                        () -> ((MessageValue)destinationMessageValue.getSelectedItem()).isIdentifierRequired()
                ),
                ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                        getLabeledField("Destination Identifier Placement", destinationIdentifierPlacement),
                        destinationMessageValue,
                        () -> ((MessageValue)destinationMessageValue.getSelectedItem()).isIdentifierRequired()
                )
        );
    }

    private void onDestinationMessageValueChanged(ActionEvent actionEvent) {
        model.setDestinationMessageValue((MessageValue) destinationMessageValue.getSelectedItem());
    }

    private void onDestinationIdentifierChanged(ActionEvent actionEvent) {
        model.setDestinationIdentifier(destinationIdentifier.getText());
    }

    private void onDestinationIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setDestinationIdentifierPlacement((SetItemPlacement) destinationIdentifierPlacement.getSelectedItem());
    }
}
