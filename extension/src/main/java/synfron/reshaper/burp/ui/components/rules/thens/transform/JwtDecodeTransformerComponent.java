package synfron.reshaper.burp.ui.components.rules.thens.transform;

import synfron.reshaper.burp.core.rules.thens.entities.transform.JwtSegment;
import synfron.reshaper.burp.ui.models.rules.thens.transform.JwtDecodeTransformerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class JwtDecodeTransformerComponent extends TransformerComponent<JwtDecodeTransformerModel> {

    private JComboBox<JwtSegment> segment;

    public JwtDecodeTransformerComponent(JwtDecodeTransformerModel model) {
        super(model);
    }

    protected void initComponent() {
        segment = createComboBox(JwtSegment.values());

        segment.setSelectedItem(model.getSegment());

        segment.addActionListener(this::onSegmentChanged);

        add(getLabeledField("Segment", segment), "wrap");
    }

    private void onSegmentChanged(ActionEvent actionEvent) {
        model.setSegment((JwtSegment)segment.getSelectedItem());
    }
}
