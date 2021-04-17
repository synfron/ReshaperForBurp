package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.rules.thens.ThenSetEventDirection;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetEventDirectionModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenSetEventDirectionComponent extends ThenComponent<ThenSetEventDirectionModel, ThenSetEventDirection> {
    private JComboBox<DataDirection> dataDirection;

    public ThenSetEventDirectionComponent(ThenSetEventDirectionModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        dataDirection = new JComboBox<>(DataDirection.values());
        JButton validate = new JButton("Validate");

        dataDirection.setSelectedItem(model.getDataDirection());

        dataDirection.addActionListener(this::onSetEventDirectionChanged);
        validate.addActionListener(this::onValidate);

        mainContainer.add(getLabeledField("Event Direction", dataDirection), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onSetEventDirectionChanged(ActionEvent actionEvent) {
        model.setSetDataDirection((DataDirection) dataDirection.getSelectedItem());
    }
}
