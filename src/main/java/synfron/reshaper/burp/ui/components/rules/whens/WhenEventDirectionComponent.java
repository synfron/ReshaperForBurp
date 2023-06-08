package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.ui.models.rules.whens.WhenEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenEventDirectionComponent extends WhenComponent<WhenEventDirectionModel, WhenEventDirection> {
    private JComboBox<HttpDataDirection> dataDirection;

    public WhenEventDirectionComponent(ProtocolType protocolType, WhenEventDirectionModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        dataDirection = createComboBox(HttpDataDirection.values());

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onDataDirectionChanged);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDataDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((HttpDataDirection)dataDirection.getSelectedItem());
    }
}
