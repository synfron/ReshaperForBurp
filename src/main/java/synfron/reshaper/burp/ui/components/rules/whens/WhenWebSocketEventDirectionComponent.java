package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenWebSocketEventDirection;
import synfron.reshaper.burp.ui.models.rules.whens.WhenWebSocketEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenWebSocketEventDirectionComponent extends WhenComponent<WhenWebSocketEventDirectionModel, WhenWebSocketEventDirection> {
    private JComboBox<WebSocketDataDirection> dataDirection;

    public WhenWebSocketEventDirectionComponent(ProtocolType protocolType, WhenWebSocketEventDirectionModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        dataDirection = createComboBox(WebSocketDataDirection.values());

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onDataDirectionChanged);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onDataDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((WebSocketDataDirection)dataDirection.getSelectedItem());
    }
}
