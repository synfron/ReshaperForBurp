package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.ui.models.rules.thens.generate.IUnixTimestampGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UnixTimestampGeneratorComponent extends GeneratorComponent<IUnixTimestampGeneratorModel> {

    private JTextField format;
    private JTextField minTimestamp;
    private JTextField maxTimestamp;

    public UnixTimestampGeneratorComponent(IUnixTimestampGeneratorModel model, boolean allowVariableTags) {
        super(model, allowVariableTags);
    }

    protected void initComponent() {
        format = createTextField(allowVariableTags);
        minTimestamp = createTextField(allowVariableTags);
        maxTimestamp = createTextField(allowVariableTags);

        format.setText(model.getFormat());
        minTimestamp.setText(model.getMinTimestamp());
        maxTimestamp.setText(model.getMaxTimestamp());

        format.getDocument().addDocumentListener(new DocumentActionListener(this::onFormatChanged));
        minTimestamp.getDocument().addDocumentListener(new DocumentActionListener(this::onMinTimestampChanged));
        maxTimestamp.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxTimestampChanged));

        add(getLabeledField("Min/Max Timestamp Format", format), "wrap");
        add(getLabeledField("Min Timestamp", minTimestamp), "wrap");
        add(getLabeledField("Max Timestamp (Exclusive)", maxTimestamp), "wrap");
    }

    private void onFormatChanged(ActionEvent actionEvent) {
        model.setFormat(format.getText());
    }

    private void onMinTimestampChanged(ActionEvent actionEvent) {
        model.setMinTimestamp(minTimestamp.getText());
    }

    private void onMaxTimestampChanged(ActionEvent actionEvent) {
        model.setMaxTimestamp(maxTimestamp.getText());
    }
}
