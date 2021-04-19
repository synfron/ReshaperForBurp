package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenLog;
import synfron.reshaper.burp.ui.models.rules.thens.ThenLogModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenLogComponent extends ThenComponent<ThenLogModel, ThenLog> {
    private JTextField text;

    public ThenLogComponent(ThenLogModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        text = new JTextField();

        text.setText(model.getText());

        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));

        mainContainer.add(getLabeledField("Text", text), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }
}
