package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TextTransform;
import synfron.reshaper.burp.ui.models.rules.thens.transform.HexTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HexTransformerComponent extends TransformerComponent<HexTransformerModel> {

    private JComboBox<TextTransform> action;
    private JComboBox<String> encoding;

    public HexTransformerComponent(HexTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        action = createComboBox(TextTransform.values());
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]), true);

        action.setSelectedItem(model.getAction());
        encoding.setSelectedItem(model.getEncoding());

        action.addActionListener(this::onActionChanged);
        encoding.addActionListener(this::onEncodingChanged);

        add(getLabeledField("Action", action), "wrap");
        add(getLabeledField("Encoding", encoding), "wrap");
    }

    private void onActionChanged(ActionEvent actionEvent) {
        model.setAction((TextTransform)action.getSelectedItem());
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }
}
