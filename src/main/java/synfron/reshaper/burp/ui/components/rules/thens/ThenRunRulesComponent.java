package synfron.reshaper.burp.ui.components.rules.thens;

import burp.BurpExtender;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.rules.thens.ThenRunRules;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunRulesModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Stream;

public class ThenRunRulesComponent extends ThenComponent<ThenRunRulesModel, ThenRunRules> {
    private JComboBox<String> ruleName;
    private JCheckBox runSingle;

    public ThenRunRulesComponent(ProtocolType protocolType, ThenRunRulesModel then) {
        super(protocolType, then);
        initComponent();
    }

    private RulesEngine getRulesEngine(ProtocolType protocolType) {
        return switch (protocolType) {
            case Http -> BurpExtender.getHttpConnector().getRulesEngine();
            case WebSocket -> BurpExtender.getWebSocketConnector().getRulesEngine();
            default -> throw new UnsupportedOperationException("ProtocolType not supported here");
        };
    }

    private void initComponent() {
        runSingle = new JCheckBox("Run Single");
        ruleName = createComboBox(Stream.concat(
                Arrays.stream(getRulesEngine(protocolType).getRulesRegistry().getRules()).map(Rule::getName),
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
