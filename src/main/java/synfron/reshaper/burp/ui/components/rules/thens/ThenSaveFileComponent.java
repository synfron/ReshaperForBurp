package synfron.reshaper.burp.ui.components.rules.thens;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.ThenHighlight;
import synfron.reshaper.burp.core.rules.thens.ThenSaveFile;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSaveFileModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSaveFileComponent extends ThenComponent<ThenSaveFileModel, ThenSaveFile> {
    private JTextField filePath;
    private JTextField text;
    private JTextField encoding;
    private JComboBox<FileExistsAction> fileExistsAction;

    public ThenSaveFileComponent(ThenSaveFileModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        filePath = new JTextField();
        text = new JTextField();
        encoding = new JTextField();
        fileExistsAction = new JComboBox<>(FileExistsAction.values());

        filePath.setText(model.getFilePath());
        text.setText(model.getText());
        encoding.setText(model.getEncoding());
        fileExistsAction.setSelectedItem(model.getFileExistsAction());

        filePath.getDocument().addDocumentListener(new DocumentActionListener(this::onFilePathChanged));
        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        encoding.getDocument().addDocumentListener(new DocumentActionListener(this::onEncodingChanged));
        fileExistsAction.addActionListener(this::onFileExistsActionChanged);

        mainContainer.add(getLabeledField("File Path", filePath), "wrap");
        mainContainer.add(getLabeledField("Text", text), "wrap");
        mainContainer.add(getLabeledField("Encoding", encoding), "wrap");
        mainContainer.add(getLabeledField("File Exists Action", fileExistsAction), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onFilePathChanged(ActionEvent actionEvent) {
        model.setFilePath(filePath.getText());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding(encoding.getText());
    }

    private void onFileExistsActionChanged(ActionEvent actionEvent) {
        model.setFileExistsAction((FileExistsAction) fileExistsAction.getSelectedItem());
    }
}
