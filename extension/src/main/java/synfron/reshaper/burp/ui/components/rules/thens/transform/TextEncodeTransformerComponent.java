package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.rules.thens.entities.transform.EncodeTransform;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EncoderType;
import synfron.reshaper.burp.ui.models.rules.thens.transform.TextEncodeTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TextEncodeTransformerComponent extends TransformerComponent<TextEncodeTransformerModel> {

    private JComboBox<EncoderType> encoderType;
    private JComboBox<EncodeTransform> action;

    public TextEncodeTransformerComponent(TextEncodeTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        action = createComboBox(EncodeTransform.values());
        encoderType = createComboBox(EncoderType.values());

        encoderType.setSelectedItem(model.getEncoderType());
        action.setSelectedItem(model.getAction());

        encoderType.addActionListener(this::onEncoderTypeChanged);
        action.addActionListener(this::onActionChanged);

        add(getLabeledField("Encoder Type", encoderType), "wrap");
        add(getLabeledField("Action", action), "wrap");
    }

    private void onActionChanged(ActionEvent actionEvent) {
        model.setAction((EncodeTransform)action.getSelectedItem());
    }

    private void onEncoderTypeChanged(ActionEvent actionEvent) {
        model.setEncoderType((EncoderType)encoderType.getSelectedItem());
    }
}
