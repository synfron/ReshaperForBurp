package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.ui.models.rules.whens.WhenEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenEventDirectionComponent extends WhenComponent<WhenEventDirectionModel, WhenEventDirection> {
    private JComboBox<DataDirection> dataDirection;

    public WhenEventDirectionComponent(WhenEventDirectionModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        dataDirection = new JComboBox<>(DataDirection.values());
        JButton save = new JButton("Save");

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onDataDirectionChanged);
        save.addActionListener(this::onSave);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(save));
    }

    private void onDataDirectionChanged(ActionEvent actionEvent) {
        model.setDataDirection((DataDirection)dataDirection.getSelectedItem());
    }
}
