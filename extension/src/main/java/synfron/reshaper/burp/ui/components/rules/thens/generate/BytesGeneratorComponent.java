package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.ui.models.rules.thens.generate.BytesGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BytesGeneratorComponent extends GeneratorComponent<BytesGeneratorModel> {

    private JTextField length;

    public BytesGeneratorComponent(BytesGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        length = createTextField(true);

        length.setText(model.getLength());

        length.getDocument().addDocumentListener(new DocumentActionListener(this::onLengthChanged));

        add(getLabeledField("Length *", length), "wrap");
    }

    private void onLengthChanged(ActionEvent actionEvent) {
        model.setLength(length.getText());
    }
}
