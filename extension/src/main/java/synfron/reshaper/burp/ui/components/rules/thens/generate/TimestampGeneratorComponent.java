package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.ui.models.rules.thens.generate.TimestampGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TimestampGeneratorComponent extends GeneratorComponent<TimestampGeneratorModel> {

    private JTextField format;
    private JTextField minTimestamp;
    private JTextField maxTimestamp;

    public TimestampGeneratorComponent(TimestampGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        format = createTextField(true);
        minTimestamp = createTextField(true);
        maxTimestamp = createTextField(true);

        format.setText(model.getFormat());
        minTimestamp.setText(model.getMinTimestamp());
        maxTimestamp.setText(model.getMaxTimestamp());

        format.getDocument().addDocumentListener(new DocumentActionListener(this::onFormatChanged));
        minTimestamp.getDocument().addDocumentListener(new DocumentActionListener(this::onMinTimestampChanged));
        maxTimestamp.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxTimestampChanged));

        add(getLabeledField("Format", format), "wrap");
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
