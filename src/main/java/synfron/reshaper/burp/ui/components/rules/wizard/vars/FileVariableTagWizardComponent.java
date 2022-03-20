package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.FileVariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FileVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final FileVariableTagWizardModel model;
    private JComboBox<String> encoding;
    private JTextField filePath;

    public FileVariableTagWizardComponent(FileVariableTagWizardModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);
        JPanel fileBrowser = getFileBrowser();

        encoding.setSelectedItem(model.getEncoding());

        encoding.addActionListener(this::onEncodingChanged);

        add(getLabeledField("Encoding *", encoding), "wrap");
        add(getLabeledField("File Path *", fileBrowser));
    }

    private JPanel getFileBrowser() {
        JPanel container = new JPanel(new MigLayout());

        filePath = createTextField(true);
        JButton browse = new JButton("Browse");

        filePath.setText(model.getFilePath());

        filePath.getDocument().addDocumentListener(new DocumentActionListener(this::onFilePathChanged));
        browse.addActionListener(this::onBrowse);

        container.add(filePath);
        container.add(getPaddedButton(browse));

        return container;
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }

    private void onFilePathChanged(ActionEvent actionEvent) {
        model.setFilePath(filePath.getText());
    }

    private void onBrowse(ActionEvent actionEvent) {
        try {
            JFileChooser fileChooser = createFileChooser("Select File");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ignored) {
        }
    }

    private JFileChooser createFileChooser(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }
}
