package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenSetEncoding;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetEncodingModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSetEncodingComponent extends ThenComponent<ThenSetEncodingModel, ThenSetEncoding> {
    private JComboBox<String> encoding;

    public ThenSetEncodingComponent(ProtocolType protocolType, ThenSetEncodingModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        encoding = createComboBox(Encoder.getEncodings().toArray(new String[0]));

        encoding.setSelectedItem(model.getEncoding());

        encoding.addActionListener(this::onEncodingChanged);

        mainContainer.add(getLabeledField("Encoding", encoding), "wrap");
    }

    private void onEncodingChanged(ActionEvent actionEvent) {
        model.setEncoding((String) encoding.getSelectedItem());
    }
}
