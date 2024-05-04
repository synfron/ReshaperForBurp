package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.ui.models.rules.thens.generate.IntegerGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IntegerGeneratorComponent extends GeneratorComponent<IntegerGeneratorModel> {

    private JTextField minValue;
    private JTextField maxValue;
    private JTextField base;

    public IntegerGeneratorComponent(IntegerGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        minValue = createTextField(true);
        maxValue = createTextField(true);
        base = createTextField(true);

        minValue.setText(model.getMinValue());
        maxValue.setText(model.getMaxValue());
        base.setText(model.getBase());

        minValue.getDocument().addDocumentListener(new DocumentActionListener(this::onMinValueChanged));
        maxValue.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxValueChanged));
        base.getDocument().addDocumentListener(new DocumentActionListener(this::onBaseChanged));

        add(getLabeledField("Min Value *", minValue), "wrap");
        add(getLabeledField("Max Value (Exclusive) *", maxValue), "wrap");
        add(getLabeledField("Base", base), "wrap");
    }

    private void onMinValueChanged(ActionEvent actionEvent) {
        model.setMinValue(minValue.getText());
    }

    private void onMaxValueChanged(ActionEvent actionEvent) {
        model.setMaxValue(maxValue.getText());
    }

    private void onBaseChanged(ActionEvent actionEvent) {
        model.setBase(base.getText());
    }
}
