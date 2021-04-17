package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.ThenBreak;
import synfron.reshaper.burp.ui.models.rules.thens.ThenBreakModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenBreakComponent extends ThenComponent<ThenBreakModel, ThenBreak> {
    private JComboBox<RuleResponse> breakType;

    public ThenBreakComponent(ThenBreakModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        breakType = new JComboBox<>(RuleResponse.getValues().stream().skip(1).toArray(RuleResponse[]::new));
        JButton validate = new JButton("Validate");

        breakType.setSelectedItem(model.getBreakType());

        breakType.addActionListener(this::onBreakTypeChanged);
        validate.addActionListener(this::onValidate);

        mainContainer.add(getLabeledField("Break Type", breakType), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onBreakTypeChanged(ActionEvent actionEvent) {
        model.setBreakType((RuleResponse)breakType.getSelectedItem());
    }
}
