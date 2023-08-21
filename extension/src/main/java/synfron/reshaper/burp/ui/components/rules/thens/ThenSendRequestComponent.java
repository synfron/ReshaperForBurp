package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSendRequest;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendRequestModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSendRequestComponent extends ThenComponent<ThenSendRequestModel, ThenSendRequest> {
    private JTextField request;
    private JTextField url;
    private JTextField protocol;
    private JTextField address;
    private JTextField port;
    private JCheckBox waitForCompletion;
    private JTextField failAfter;
    private JCheckBox failOnErrorStatusCode;
    private JCheckBox breakAfterFailure;
    private JCheckBox captureOutput;
    private JCheckBox captureAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenSendRequestComponent(ProtocolType protocolType, ThenSendRequestModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        request = createTextField(true);
        url = createTextField(true);
        protocol = createTextField(true);
        address = createTextField(true);
        port = createTextField(true);
        waitForCompletion = new JCheckBox("Wait for Completion");
        failAfter = createTextField(true);
        failOnErrorStatusCode = new JCheckBox("Fail on Error Status Code");
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureOutput = new JCheckBox("Capture Output");
        captureAfterFailure = new JCheckBox("Capture After Failure");
        captureVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        captureVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        request.setText(model.getRequest());
        url.setText(model.getUrl());
        protocol.setText(model.getProtocol());
        address.setText(model.getAddress());
        port.setText(model.getPort());
        waitForCompletion.setSelected(model.isWaitForCompletion());
        failAfter.setText(model.getFailAfter());
        failOnErrorStatusCode.setSelected(model.isFailOnErrorStatusCode());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureOutput.setSelected(model.isCaptureOutput());
        captureAfterFailure.setSelected(model.isCaptureAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        request.getDocument().addDocumentListener(new DocumentActionListener(this::onRequestChanged));
        url.getDocument().addDocumentListener(new DocumentActionListener(this::onUrlChanged));
        protocol.getDocument().addDocumentListener(new DocumentActionListener(this::onProtocolChanged));
        address.getDocument().addDocumentListener(new DocumentActionListener(this::onAddressChanged));
        port.getDocument().addDocumentListener(new DocumentActionListener(this::onPortChanged));
        waitForCompletion.addActionListener(this::onWaitForCompletionChanged);
        failAfter.getDocument().addDocumentListener(new DocumentActionListener(this::onFailAfterChanged));
        failOnErrorStatusCode.addActionListener(this::onFailOnErrorStatusCodeChanged);
        breakAfterFailure.addActionListener(this::onContinueAfterFailureChanged);
        captureOutput.addActionListener(this::onCaptureOutputChanged);
        captureAfterFailure.addActionListener(this::onCaptureAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Request", request), "wrap");
        mainContainer.add(getLabeledField("URL", url), "wrap");
        mainContainer.add(getLabeledField("Protocol", protocol), "wrap");
        mainContainer.add(getLabeledField("Address", address), "wrap");
        mainContainer.add(getLabeledField("Port", port), "wrap");
        mainContainer.add(waitForCompletion, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Fail After (milliseconds) *", failAfter),
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                failOnErrorStatusCode,
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                breakAfterFailure,
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                captureOutput,
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                captureAfterFailure,
                List.of(waitForCompletion, captureOutput),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Capture Variable Source", captureVariableSource),
                List.of(waitForCompletion, captureOutput),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Capture Variable Name *", captureVariableName),
                List.of(waitForCompletion, captureOutput),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                List.of(waitForCompletion, captureOutput, captureVariableSource),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected() && ((VariableSource)captureVariableSource.getSelectedItem()).isList()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Delimiter *", delimiter),
                List.of(waitForCompletion, captureOutput, captureVariableSource, itemPlacement),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected() && ((VariableSource)captureVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                List.of(waitForCompletion, captureOutput, captureVariableSource, itemPlacement),
                () -> waitForCompletion.isSelected() && captureOutput.isSelected() && ((VariableSource)captureVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
    }

    private void onRequestChanged(ActionEvent actionEvent) {
        model.setRequest(request.getText());
    }

    private void onUrlChanged(ActionEvent actionEvent) {
        model.setUrl(url.getText());
    }

    private void onPortChanged(ActionEvent actionEvent) {
        model.setPort(port.getText());
    }

    private void onAddressChanged(ActionEvent actionEvent) {
        model.setAddress(address.getText());
    }

    private void onProtocolChanged(ActionEvent actionEvent) {
        model.setProtocol(protocol.getText());
    }

    private void onWaitForCompletionChanged(ActionEvent actionEvent) {
        model.setWaitForCompletion(waitForCompletion.isSelected());
    }

    private void onFailAfterChanged(ActionEvent actionEvent) {
        model.setFailAfter(failAfter.getText());
    }

    private void onFailOnErrorStatusCodeChanged(ActionEvent actionEvent) {
        model.setFailOnErrorStatusCode(failOnErrorStatusCode.isSelected());
    }

    private void onContinueAfterFailureChanged(ActionEvent actionEvent) {
        model.setBreakAfterFailure(breakAfterFailure.isSelected());
    }

    private void onCaptureOutputChanged(ActionEvent actionEvent) {
        model.setCaptureOutput(captureOutput.isSelected());
    }

    private void onCaptureAfterFailureChanged(ActionEvent actionEvent) {
        model.setCaptureAfterFailure(captureAfterFailure.isSelected());
    }

    private void onCaptureVariableSourceChanged(ActionEvent actionEvent) {
        model.setCaptureVariableSource((VariableSource) captureVariableSource.getSelectedItem());
    }

    private void onCaptureVariableNameChanged(ActionEvent actionEvent) {
        model.setCaptureVariableName(captureVariableName.getText());
    }

    private void onItemPlacementChanged(ActionEvent actionEvent) {
        model.setItemPlacement((SetListItemPlacement)itemPlacement.getSelectedItem());
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onIndexChanged(ActionEvent actionEvent) {
        model.setIndex(index.getText());
    }
}
