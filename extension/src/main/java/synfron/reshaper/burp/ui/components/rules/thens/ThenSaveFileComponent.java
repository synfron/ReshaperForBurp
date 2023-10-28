package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenSaveFile;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSaveFileModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSaveFileComponent extends ThenComponent<ThenSaveFileModel, ThenSaveFile> {
    private JTextField filePath;
    private JTextField text;
    private JComboBox<String> encoding;
    private JComboBox<FileExistsAction> fileExistsAction;

    public ThenSaveFileComponent(ProtocolType protocolType, ThenSaveFileModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        filePath = createTextField(true);
        text = createTextField(true);
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);
        fileExistsAction = createComboBox(FileExistsAction.values());

        filePath.setText(model.getFilePath());
        text.setText(model.getText());
        encoding.setSelectedItem(model.getEncoding());
        fileExistsAction.setSelectedItem(model.getFileExistsAction());

        filePath.getDocument().addDocumentListener(new DocumentActionListener(this::onFilePathChanged));
        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        encoding.addActionListener(this::onEncodingChanged);
        fileExistsAction.addActionListener(this::onFileExistsActionChanged);

        mainContainer.add(getLabeledField("File Path *", filePath), "wrap");
        mainContainer.add(getLabeledField("Text", text), "wrap");
        mainContainer.add(getLabeledField("Encoding *", encoding), "wrap");
        mainContainer.add(getLabeledField("File Exists Action", fileExistsAction), "wrap");
    }

    private void onFilePathChanged(ActionEvent actionEvent) {
        model.setFilePath(filePath.getText());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }

    private void onFileExistsActionChanged(ActionEvent actionEvent) {
        model.setFileExistsAction((FileExistsAction) fileExistsAction.getSelectedItem());
    }
}
