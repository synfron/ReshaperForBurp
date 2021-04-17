package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenDrop;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDropModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenDropComponent extends ThenComponent<ThenDropModel, ThenDrop> {
    private JCheckBox dropMessage;

    public ThenDropComponent(ThenDropModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        dropMessage = new JCheckBox("Drop Message");
        JButton validate = new JButton("Validate");

        dropMessage.setSelected(model.isDropMessage());

        dropMessage.addActionListener(this::onDropMessageChanged);
        validate.addActionListener(this::onValidate);

        mainContainer.add(dropMessage, "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDropMessageChanged(ActionEvent actionEvent) {
        model.setDropMessage(dropMessage.isSelected());
    }
}
