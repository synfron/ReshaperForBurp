package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRunProcess;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunProcessModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenRunProcessComponent extends ThenComponent<ThenRunProcessModel, ThenRunProcess> {
    private JTextField command;
    private JTextField input;
    private JCheckBox waitForCompletion;
    private JTextField failAfter;
    private JCheckBox killAfterFailure;
    private JCheckBox failOnNonZeroExitCode;
    private JCheckBox breakAfterFailure;
    private JCheckBox captureOutput;
    private JCheckBox captureAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenRunProcessComponent(ProtocolType protocolType, ThenRunProcessModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        command = createTextField(true);
        input = createTextField(true);
        waitForCompletion = new JCheckBox("Wait for Completion");
        failAfter = createTextField(true);
        killAfterFailure = new JCheckBox("Kill After Failure");
        failOnNonZeroExitCode = new JCheckBox("Fail on Non-Zero Exit Code");
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureOutput = new JCheckBox("Capture Output");
        captureAfterFailure = new JCheckBox("Capture After Failure");
        captureVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        captureVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        command.setText(model.getCommand());
        input.setText(model.getInput());
        waitForCompletion.setSelected(model.isWaitForCompletion());
        failAfter.setText(model.getFailAfter());
        killAfterFailure.setSelected(model.isKillAfterFailure());
        failOnNonZeroExitCode.setSelected(model.isFailOnNonZeroExitCode());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureOutput.setSelected(model.isCaptureOutput());
        captureAfterFailure.setSelected(model.isCaptureAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        command.getDocument().addDocumentListener(new DocumentActionListener(this::onCommandChanged));
        input.getDocument().addDocumentListener(new DocumentActionListener(this::onInputChanged));
        waitForCompletion.addActionListener(this::onWaitForCompletionChanged);
        failAfter.getDocument().addDocumentListener(new DocumentActionListener(this::onFailAfterChanged));
        killAfterFailure.addActionListener(this::onKillAfterWaitChanged);
        failOnNonZeroExitCode.addActionListener(this::onFailOnNonZeroExitCodeChanged);
        breakAfterFailure.addActionListener(this::onBreakAfterFailureChanged);
        captureOutput.addActionListener(this::onCaptureOutputChanged);
        captureAfterFailure.addActionListener(this::onCaptureAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Command *", command), "wrap");
        mainContainer.add(getLabeledField("Stdin", input), "wrap");
        mainContainer.add(waitForCompletion, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Fail After (milliseconds) *", failAfter),
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                failOnNonZeroExitCode,
                waitForCompletion,
                () -> waitForCompletion.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                killAfterFailure,
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

    private void onCommandChanged(ActionEvent actionEvent) {
        model.setCommand(command.getText());
    }

    private void onInputChanged(ActionEvent actionEvent) {
        model.setInput(input.getText());
    }

    private void onWaitForCompletionChanged(ActionEvent actionEvent) {
        model.setWaitForCompletion(waitForCompletion.isSelected());
    }

    private void onFailAfterChanged(ActionEvent actionEvent) {
        model.setFailAfter(failAfter.getText());
    }

    private void onKillAfterWaitChanged(ActionEvent actionEvent) {
        model.setKillAfterFailure(killAfterFailure.isSelected());
    }

    private void onFailOnNonZeroExitCodeChanged(ActionEvent actionEvent) {
        model.setFailOnNonZeroExitCode(failOnNonZeroExitCode.isSelected());
    }

    private void onBreakAfterFailureChanged(ActionEvent actionEvent) {
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
