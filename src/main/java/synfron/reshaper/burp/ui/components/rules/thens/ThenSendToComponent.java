package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenSendTo;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendToModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
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

    public ThenSendToComponent(ThenSendToModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        sendTo = new JComboBox<>(SendToOption.values());
        overrideDefaults = new JCheckBox("Override Defaults");
        host = new JTextField();
        port = new JTextField();
        protocol = new JTextField();
        request = new JTextField();
        value = new JTextField();
        url = new JTextField();

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
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Host", host),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                            sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Port", port),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Protocol", protocol),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Request", request),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Intruder ||
                                sendTo.getSelectedItem() == SendToOption.Repeater
                )
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Value", value),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && sendTo.getSelectedItem() == SendToOption.Comparer
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("URL", url),
                List.of(overrideDefaults, sendTo),
                () -> overrideDefaults.isSelected() && (
                        sendTo.getSelectedItem() == SendToOption.Spider ||
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
