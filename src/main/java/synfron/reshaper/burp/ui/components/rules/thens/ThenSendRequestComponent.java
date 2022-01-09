package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenSendRequest;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSendRequestModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSendRequestComponent extends ThenComponent<ThenSendRequestModel, ThenSendRequest> {
    private JTextField protocol;
    private JTextField address;
    private JTextField port;
    private JTextField request;
    private JCheckBox waitForCompletion;
    private JTextField failAfter;
    private JCheckBox failOnErrorStatusCode;
    private JCheckBox breakAfterFailure;
    private JCheckBox captureOutput;
    private JCheckBox captureAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;

    public ThenSendRequestComponent(ThenSendRequestModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        protocol = createTextField();
        address = createTextField();
        port = createTextField();
        request = createTextField();
        waitForCompletion = new JCheckBox("Wait for Completion");
        failAfter = createTextField();
        failOnErrorStatusCode = new JCheckBox("Fail on Error Status Code");
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureOutput = new JCheckBox("Capture Output");
        captureAfterFailure = new JCheckBox("Capture After Failure");
        captureVariableSource = new JComboBox<>(new VariableSource[] { VariableSource.Event, VariableSource.Global });
        captureVariableName = createTextField();

        protocol.setText(model.getProtocol());
        address.setText(model.getAddress());
        port.setText(model.getPort());
        request.setText(model.getRequest());
        waitForCompletion.setSelected(model.isWaitForCompletion());
        failAfter.setText(model.getFailAfter());
        failOnErrorStatusCode.setSelected(model.isFailOnErrorStatusCode());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureOutput.setSelected(model.isCaptureOutput());
        captureAfterFailure.setSelected(model.isCaptureAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());

        protocol.getDocument().addDocumentListener(new DocumentActionListener(this::onProtocolChanged));
        address.getDocument().addDocumentListener(new DocumentActionListener(this::onAddressChanged));
        port.getDocument().addDocumentListener(new DocumentActionListener(this::onPortChanged));
        request.getDocument().addDocumentListener(new DocumentActionListener(this::onRequestChanged));
        waitForCompletion.addActionListener(this::onWaitForCompletionChanged);
        failAfter.getDocument().addDocumentListener(new DocumentActionListener(this::onFailAfterChanged));
        failOnErrorStatusCode.addActionListener(this::onFailOnErrorStatusCodeChanged);
        breakAfterFailure.addActionListener(this::onContinueAfterFailureChanged);
        captureOutput.addActionListener(this::onCaptureOutputChanged);
        captureAfterFailure.addActionListener(this::onCaptureAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));

        mainContainer.add(getLabeledField("Protocol", protocol), "wrap");
        mainContainer.add(getLabeledField("Address", address), "wrap");
        mainContainer.add(getLabeledField("Port", port), "wrap");
        mainContainer.add(getLabeledField("Request", request), "wrap");
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
        mainContainer.add(getPaddedButton(validate));
    }

    private void onRequestChanged(ActionEvent actionEvent) {
        model.setRequest(request.getText());
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
}
