package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.entities.transform.Base64Variant;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EncodeTransform;
import synfron.reshaper.burp.ui.models.rules.thens.transform.Base64TransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Base64TransformerComponent extends TransformerComponent<Base64TransformerModel> {

    private JComboBox<Base64Variant> variant;
    private JComboBox<EncodeTransform> action;
    private JComboBox<String> encoding;

    public Base64TransformerComponent(Base64TransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        action = createComboBox(EncodeTransform.values());
        variant = createComboBox(Base64Variant.values());
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);

        variant.setSelectedItem(model.getVariant());
        action.setSelectedItem(model.getAction());
        encoding.setSelectedItem(model.getEncoding());

        variant.addActionListener(this::onVariantChanged);
        action.addActionListener(this::onActionChanged);
        encoding.addActionListener(this::onEncodingChanged);

        add(getLabeledField("Variant", variant), "wrap");
        add(getLabeledField("Action", action), "wrap");
        add(getLabeledField("Encoding", encoding), "wrap");
    }

    private void onActionChanged(ActionEvent actionEvent) {
        model.setAction((EncodeTransform)action.getSelectedItem());
    }

    private void onVariantChanged(ActionEvent actionEvent) {
        model.setVariant((Base64Variant)variant.getSelectedItem());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }
}
