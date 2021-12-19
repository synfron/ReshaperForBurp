package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenRunRules;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunRulesModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenRunRulesComponent extends ThenComponent<ThenRunRulesModel, ThenRunRules> {
    private JTextField ruleName;
    private JCheckBox runSingle;

    public ThenRunRulesComponent(ThenRunRulesModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        runSingle = new JCheckBox("Run Single");
        ruleName = new JTextField();

        runSingle.setSelected(model.isRunSingle());
        ruleName.setText(model.getRuleName());

        runSingle.addActionListener(this::onRunSingleChanged);
        ruleName.getDocument().addDocumentListener(new DocumentActionListener(this::onRuleNameChanged));

        mainContainer.add(runSingle, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Rule Name *", ruleName),
                runSingle,
                () -> runSingle.isSelected()
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onRunSingleChanged(ActionEvent actionEvent) {
        model.setRunSingle(runSingle.isSelected());
    }

    private void onRuleNameChanged(ActionEvent actionEvent) {
        model.setRuleName(ruleName.getText());
    }
}
