package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenPrompt;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenPromptModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenPromptComponent extends ThenComponent<ThenPromptModel, ThenPrompt> {
    private JTextField description;
    private JTextField starterText;
    private JTextField failAfter;
    private JCheckBox breakAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenPromptComponent(ProtocolType protocolType, ThenPromptModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        description = createTextField(true);
        starterText = createTextField(true);
        failAfter = createTextField(true);
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        captureVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        description.setText(model.getDescription());
        starterText.setText(model.getStarterText());
        failAfter.setText(model.getFailAfter());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        description.getDocument().addDocumentListener(new DocumentActionListener(this::onDescriptionChanged));
        starterText.getDocument().addDocumentListener(new DocumentActionListener(this::onStarterTextChanged));
        failAfter.getDocument().addDocumentListener(new DocumentActionListener(this::onFailAfterChanged));
        breakAfterFailure.addActionListener(this::onContinueAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Description *", description), "wrap");
        mainContainer.add(getLabeledField("Starter Text", starterText), "wrap");
        mainContainer.add(getLabeledField("Fail After (milliseconds) *", failAfter), "wrap");
        mainContainer.add(breakAfterFailure, "wrap");
        mainContainer.add(getLabeledField("Capture Variable Source", captureVariableSource), "wrap");
        mainContainer.add(getLabeledField("Capture Variable Name *", captureVariableName), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                captureVariableSource,
                () -> ((VariableSource)captureVariableSource.getSelectedItem()).isList()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Delimiter *", delimiter),
                List.of(captureVariableSource, itemPlacement),
                () -> ((VariableSource)captureVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                List.of(captureVariableSource, itemPlacement),
                () -> ((VariableSource)captureVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
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
