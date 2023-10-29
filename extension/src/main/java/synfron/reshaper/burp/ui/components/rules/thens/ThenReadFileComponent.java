package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenReadFile;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenReadFileModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenReadFileComponent extends ThenComponent<ThenReadFileModel, ThenReadFile> {
    private JTextField filePath;
    private JComboBox<String> encoding;
    private JCheckBox breakAfterFailure;
    private JCheckBox captureAfterFailure;
    private JComboBox<VariableSource> captureVariableSource;
    private JTextField captureVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenReadFileComponent(ProtocolType protocolType, ThenReadFileModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        filePath = createTextField(true);
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);
        breakAfterFailure = new JCheckBox("Break After Failure");
        captureAfterFailure = new JCheckBox("Capture After Failure");
        captureVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        captureVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        filePath.setText(model.getFilePath());
        encoding.setSelectedItem(model.getEncoding());
        breakAfterFailure.setSelected(model.isBreakAfterFailure());
        captureAfterFailure.setSelected(model.isCaptureAfterFailure());
        captureVariableSource.setSelectedItem(model.getCaptureVariableSource());
        captureVariableName.setText(model.getCaptureVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        filePath.getDocument().addDocumentListener(new DocumentActionListener(this::onFilePathChanged));
        encoding.addActionListener(this::onEncodingChanged);
        breakAfterFailure.addActionListener(this::onBreakAfterFailureChanged);
        captureAfterFailure.addActionListener(this::onCaptureAfterFailureChanged);
        captureVariableSource.addActionListener(this::onCaptureVariableSourceChanged);
        captureVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onCaptureVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("File Path *", filePath), "wrap");
        mainContainer.add(getLabeledField("Encoding *", encoding), "wrap");
        mainContainer.add(breakAfterFailure, "wrap");
        mainContainer.add(captureAfterFailure, "wrap");
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
    }

    private void onFilePathChanged(ActionEvent actionEvent) {
        model.setFilePath(filePath.getText());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }

    private void onBreakAfterFailureChanged(ActionEvent actionEvent) {
        model.setBreakAfterFailure(breakAfterFailure.isSelected());
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
