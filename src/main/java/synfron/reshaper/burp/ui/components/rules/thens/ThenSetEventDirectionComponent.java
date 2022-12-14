package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSetEventDirection;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSetEventDirectionComponent extends ThenComponent<ThenSetEventDirectionModel, ThenSetEventDirection> {
    private JComboBox<HttpDataDirection> dataDirection;

    public ThenSetEventDirectionComponent(ProtocolType protocolType, ThenSetEventDirectionModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        dataDirection = createComboBox(HttpDataDirection.values());

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onSetEventDirectionChanged);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onSetEventDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((HttpDataDirection) dataDirection.getSelectedItem());
    }
}
