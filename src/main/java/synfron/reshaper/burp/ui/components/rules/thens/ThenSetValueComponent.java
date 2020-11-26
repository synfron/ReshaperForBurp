package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.thens.ThenSetValue;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetValueModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSetValueComponent extends ThenSetComponent<ThenSetValueModel, ThenSetValue> {

    private JComboBox<MessageValue> destinationMessageValue;
    private JTextField destinationIdentifier;

    public ThenSetValueComponent(ThenSetValueModel then) {
        super(then);
    }

    @Override
    protected List<Component> getExtendedComponents() {
        destinationMessageValue = new JComboBox<>(MessageValue.values());
        destinationIdentifier = new JTextField();

        destinationMessageValue.setSelectedItem(model.getDestinationMessageValue());
        destinationIdentifier.setText(model.getDestinationIdentifier());

        destinationMessageValue.addActionListener(this::onDestinationMessageValueChanged);
        destinationIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationIdentifierChanged));

        return List.of(
                getLabeledField("Destination Message Value", destinationMessageValue),
                withVisibilityFieldChangeDependency(
                        getLabeledField("Destination Identifier", destinationIdentifier),
                        destinationMessageValue,
                        () -> MessageValueHandler.hasIdentifier((MessageValue)destinationMessageValue.getSelectedItem())
                )
        );
    }

    private void onDestinationMessageValueChanged(ActionEvent actionEvent) {
        model.setDestinationMessageValue((MessageValue) destinationMessageValue.getSelectedItem());
    }

    private void onDestinationIdentifierChanged(ActionEvent actionEvent) {
        model.setDestinationIdentifier(destinationIdentifier.getText());
    }
}
