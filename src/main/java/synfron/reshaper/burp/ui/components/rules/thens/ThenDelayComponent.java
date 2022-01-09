package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenDelay;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDelayModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenDelayComponent extends ThenComponent<ThenDelayModel,ThenDelay> {
    private JTextField delay;

    public ThenDelayComponent(ThenDelayModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        delay = createTextField();

        delay.setText(model.getDelay());

        delay.getDocument().addDocumentListener(new DocumentActionListener(this::onDelayChanged));

        mainContainer.add(getLabeledField("Delay (milliseconds) *", delay), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDelayChanged(ActionEvent actionEvent) {
        model.setDelay(delay.getText());
    }
}
