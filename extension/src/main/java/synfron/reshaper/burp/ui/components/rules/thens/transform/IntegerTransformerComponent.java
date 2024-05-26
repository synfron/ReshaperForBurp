package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.ui.models.rules.thens.transform.IntegerTransformerModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IntegerTransformerComponent extends TransformerComponent<IntegerTransformerModel> {

    private JTextField sourceBase;
    private JTextField targetBase;

    public IntegerTransformerComponent(IntegerTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        sourceBase = createTextField(true);
        targetBase = createTextField(true);

        sourceBase.setText(model.getSourceBase());
        targetBase.setText(model.getTargetBase());

        sourceBase.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceBaseChanged));
        targetBase.getDocument().addDocumentListener(new DocumentActionListener(this::onTargetBaseChanged));

        add(getLabeledField("Source Base", sourceBase), "wrap");
        add(getLabeledField("Target Base", targetBase), "wrap");
    }

    private void onSourceBaseChanged(ActionEvent actionEvent) {
        model.setSourceBase(sourceBase.getText());
    }

    private void onTargetBaseChanged(ActionEvent actionEvent) {
        model.setTargetBase(targetBase.getText());
    }
}
