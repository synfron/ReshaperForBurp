package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenDrop;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDropModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenDropComponent extends ThenComponent<ThenDropModel, ThenDrop> {
    private JCheckBox dropMessage;

    public ThenDropComponent(ProtocolType protocolType, ThenDropModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        dropMessage = new JCheckBox("Drop Message");

        dropMessage.setSelected(model.isDropMessage());

        dropMessage.addActionListener(this::onDropMessageChanged);

        mainContainer.add(dropMessage, "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDropMessageChanged(ActionEvent actionEvent) {
        model.setDropMessage(dropMessage.isSelected());
    }
}
