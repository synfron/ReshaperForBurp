package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IBytesGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BytesGeneratorComponent extends GeneratorComponent<IBytesGeneratorModel> {

    private JTextField length;
    private JComboBox<String> encoding;

    public BytesGeneratorComponent(IBytesGeneratorModel model, boolean allowVariableTags) {
        super(model, allowVariableTags);
    }

    protected void initComponent() {
        length = createTextField(allowVariableTags);
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);

        length.setText(model.getLength());
        encoding.setSelectedItem(model.getEncoding());

        length.getDocument().addDocumentListener(new DocumentActionListener(this::onLengthChanged));
        encoding.addActionListener(this::onEncodingChanged);

        add(getLabeledField("Length *", length), "wrap");
        add(getLabeledField("Encoding", encoding), "wrap");
    }

    private void onLengthChanged(ActionEvent actionEvent) {
        model.setLength(length.getText());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }
}
