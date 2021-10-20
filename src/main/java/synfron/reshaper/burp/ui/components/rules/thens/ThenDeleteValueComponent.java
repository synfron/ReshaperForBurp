package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteValue;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDeleteValueModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThenDeleteValueComponent extends ThenComponent<ThenDeleteValueModel, ThenDeleteValue> {
    protected JComboBox<MessageValue> messageValue;
    protected JTextField identifier;
    private final Set<MessageValue> excludedMessageValues = new HashSet<>(List.of(
            MessageValue.DestinationAddress,
            MessageValue.DestinationPort,
            MessageValue.HttpProtocol,
            MessageValue.Url,
            MessageValue.HttpRequestMessage,
            MessageValue.HttpRequestMethod,
            MessageValue.HttpResponseMessage,
            MessageValue.HttpRequestStatusLine,
            MessageValue.HttpResponseStatusLine,
            MessageValue.SourceAddress,
            MessageValue.HttpRequestUri,
            MessageValue.HttpResponseStatusCode
    ));

    public ThenDeleteValueComponent(ThenDeleteValueModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        messageValue = new JComboBox<>(Arrays.stream(MessageValue.values())
                .filter(value -> !excludedMessageValues.contains(value))
                .toArray(MessageValue[]::new)
        );
        identifier = new JTextField();

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));

        mainContainer.add(getLabeledField("Message Value", messageValue), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier", identifier),
                messageValue,
                () -> MessageValueHandler.hasIdentifier((MessageValue) messageValue.getSelectedItem())
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue) messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }
}
