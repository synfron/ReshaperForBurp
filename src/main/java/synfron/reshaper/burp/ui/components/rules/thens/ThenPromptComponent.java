package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenPrompt;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenPromptModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenPromptComponent extends ThenComponent<ThenPromptModel, ThenPrompt> {
    private JTextField description;
    private JTextField starterText;
    private JTextField failAfter;
    private JCheckBox breakAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;

    public ThenPromptComponent(ThenPromptModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        description = new JTextField();
        starterText = new JTextField();
        failAfter = new JTextField();
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureVariableSource = new JComboBox<>(new VariableSource[] { VariableSource.Event, VariableSource.Global });
        captureVariableName = new JTextField();

        description.setText(model.getDescription());
        starterText.setText(model.getStarterText());
        failAfter.setText(model.getFailAfter());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());

        description.getDocument().addDocumentListener(new DocumentActionListener(this::onDescriptionChanged));
        starterText.getDocument().addDocumentListener(new DocumentActionListener(this::onStarterTextChanged));
        failAfter.getDocument().addDocumentListener(new DocumentActionListener(this::onFailAfterChanged));
        breakAfterFailure.addActionListener(this::onContinueAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));

        mainContainer.add(getLabeledField("Description", description), "wrap");
        mainContainer.add(getLabeledField("Starter Text", starterText), "wrap");
        mainContainer.add(getLabeledField("Fail After (milliseconds)", failAfter), "wrap");
        mainContainer.add(breakAfterFailure, "wrap");
        mainContainer.add(getLabeledField("Capture Variable Source", captureVariableSource), "wrap");
        mainContainer.add(getLabeledField("Capture Variable Name", captureVariableName), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDescriptionChanged(ActionEvent actionEvent) {
        model.setDescription(description.getText());
    }

    private void onStarterTextChanged(ActionEvent actionEvent) {
        model.setStarterText(starterText.getText());
    }

    private void onFailAfterChanged(ActionEvent actionEvent) {
        model.setFailAfter(failAfter.getText());
    }

    private void onContinueAfterFailureChanged(ActionEvent actionEvent) {
        model.setBreakAfterFailure(breakAfterFailure.isSelected());
    }

    private void onCaptureVariableSourceChanged(ActionEvent actionEvent) {
        model.setCaptureVariableSource((VariableSource) captureVariableSource.getSelectedItem());
    }

    private void onCaptureVariableNameChanged(ActionEvent actionEvent) {
        model.setCaptureVariableName(captureVariableName.getText());
    }
}
