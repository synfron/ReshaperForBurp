package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenSetEncoding;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetEncodingModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSetEncodingComponent extends ThenComponent<ThenSetEncodingModel, ThenSetEncoding> {
    private JComboBox<String> encoding;

    public ThenSetEncodingComponent(ThenSetEncodingModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        encoding = new JComboBox<>(Encoder.getEncodings().toArray(new String[0]));

        encoding.setSelectedItem(model.getEncoding());

        encoding.addActionListener(this::onEncodingChanged);

        mainContainer.add(getLabeledField("Encoding", encoding), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }
}
