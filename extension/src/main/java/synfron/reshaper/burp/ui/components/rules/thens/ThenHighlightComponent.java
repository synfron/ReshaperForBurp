package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.HighlightColor;
import synfron.reshaper.burp.core.rules.thens.ThenHighlight;
import synfron.reshaper.burp.ui.models.rules.thens.ThenHighlightModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenHighlightComponent extends ThenComponent<ThenHighlightModel, ThenHighlight> {
    private JComboBox<HighlightColor> color;

    public ThenHighlightComponent(ProtocolType protocolType, ThenHighlightModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        color = createComboBox(HighlightColor.values());

        color.setSelectedItem(model.getColor());

        color.addActionListener(this::onColorChanged);

        mainContainer.add(getLabeledField("Color", color), "wrap");
    }

    private void onColorChanged(ActionEvent actionEvent) {
        model.setColor((HighlightColor) color.getSelectedItem());
    }
}
