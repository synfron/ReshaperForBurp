package synfron.reshaper.burp.ui.components.rules;

import synfron.reshaper.burp.core.ProtocolType;

import javax.swing.*;
import java.awt.*;

public class RulesTabComponent extends JPanel {

    private final ProtocolType protocolType;

    public RulesTabComponent(ProtocolType protocolType) {
        this.protocolType = protocolType;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        RuleListComponent ruleList = new RuleListComponent(protocolType);
        RuleContainerComponent ruleContainer = new RuleContainerComponent(protocolType);
        ruleList.setSelectionContainer(ruleContainer);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ruleList, ruleContainer);
        splitPane.setDividerLocation(ruleList.getPreferredSize().width);
        splitPane.setDividerSize(3);

        add(splitPane);
    }
}
