package synfron.reshaper.burp.ui.components.rules.thens;

import burp.BurpExtender;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.rules.thens.ThenRunRules;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunRulesModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenRunRulesComponent extends ThenComponent<ThenRunRulesModel, ThenRunRules> {
    private JComboBox<String> ruleName;
    private JCheckBox runSingle;

    public ThenRunRulesComponent(ThenRunRulesModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        runSingle = new JCheckBox("Run Single");
        ruleName = createComboBox(Stream.concat(
                Arrays.stream(BurpExtender.getConnector().getRulesEngine().getRulesRegistry().getRules()).map(rule -> rule.getName()),
                Stream.of(model.getRuleName())
        ).filter(StringUtils::isNotEmpty).sorted().distinct().toArray(String[]::new));

        runSingle.setSelected(model.isRunSingle());
        ruleName.setSelectedItem(model.getRuleName());

        runSingle.addActionListener(this::onRunSingleChanged);
        ruleName.addActionListener(this::onRuleNameChanged);

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
        model.setRuleName((String) ruleName.getSelectedItem());
    }
}
