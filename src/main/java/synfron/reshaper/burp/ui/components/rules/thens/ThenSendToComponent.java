package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSendTo;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendToModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class ThenSendToComponent extends ThenComponent<ThenSendToModel, ThenSendTo> {
    private JComboBox<SendToOption> sendTo;
    private JCheckBox overrideDefaults;
    private JTextField host;
    private JTextField port;
    private JTextField protocol;
    private JTextField request;
    private JTextField value;
    private JTextField url;

    public ThenSendToComponent(ProtocolType protocolType, ThenSendToModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        sendTo = createComboBox(Arrays.stream(SendToOption.values())
                .filter(value -> value != SendToOption.Spider)
                .toArray(SendToOption[]::new)
        );
        overrideDefaults = new JCheckBox("Override Defaults");
        host = createTextField(true);
        port = createTextField(true);
        protocol = createTextField(true);
        request = createTextField(true);
        value = createTextField(true);
        url = createTextField(true);

        sendTo.setSelectedItem(model.getSendTo());
        overrideDefaults.setSelected(model.isOverrideDefaults());
        host.setText(model.getHost());
        port.setText(model.getPort());
        protocol.setText(model.getProtocol());
        request.setText(model.getRequest());
        value.setText(model.getValue());
        url.setText(model.getUrl());

        sendTo.addActionListener(this::onSendToChanged);
        overrideDefaults.addActionListener(this::onOverrideDefaultsChanged);
        host.getDocument().addDocumentListener(new DocumentActionListener(this::onHostChanged));
        port.getDocument().addDocumentListener(new DocumentActionListener(this::onPortChanged));
        protocol.getDocument().addDocumentListener(new DocumentActionListener(this::onProtocolChanged));
        request.getDocument().addDocumentListener(new DocumentActionListener(this::onRequestChanged));
        value.getDocument().addDocumentListener(new DocumentActionListener(this::onValueChanged));
        url.getDocument().addDocumentListener(new DocumentActionListener(this::onUrlChanged));

        mainContainer.add(getLabeledField("Send To", sendTo), "wrap");
        mainContainer.add(overrideDefaults, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Host", host),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                            sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Port", port),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Protocol", protocol),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Request", request),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Value", value),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && sendTo.getSelectedItem() == SendToOption.Comparer
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("URL", url),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Browser
                )
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onSendToChanged(ActionEvent actionEvent) {
        model.setSendTo((SendToOption)sendTo.getSelectedItem());
    }

    private void onOverrideDefaultsChanged(ActionEvent actionEvent) {
        model.setOverrideDefaults(overrideDefaults.isSelected());
    }

    private void onHostChanged(ActionEvent actionEvent) {
        model.setHost(host.getText());
    }

    private void onPortChanged(ActionEvent actionEvent) {
        model.setPort(port.getText());
    }

    private void onProtocolChanged(ActionEvent actionEvent) {
        model.setProtocol(protocol.getText());
    }

    private void onRequestChanged(ActionEvent actionEvent) {
        model.setRequest(request.getText());
    }

    private void onValueChanged(ActionEvent actionEvent) {
        model.setValue(value.getText());
    }

    private void onUrlChanged(ActionEvent actionEvent) {
        model.setUrl(url.getText());
    }
}
