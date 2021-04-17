package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenHighlight;
import synfron.reshaper.burp.ui.models.rules.thens.ThenHighlightModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenHighlightComponent extends ThenComponent<ThenHighlightModel, ThenHighlight> {
    private JComboBox<ThenHighlight.HighlightColor> color;

    public ThenHighlightComponent(ThenHighlightModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        color = new JComboBox<>(ThenHighlight.HighlightColor.values());
        JButton validate = new JButton("Validate");

        color.setSelectedItem(model.getColor());

        color.addActionListener(this::onColorChanged);
        validate.addActionListener(this::onValidate);

        mainContainer.add(getLabeledField("Color", color), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onColorChanged(ActionEvent actionEvent) {
        model.setColor((ThenHighlight.HighlightColor) color.getSelectedItem());
    }
}
