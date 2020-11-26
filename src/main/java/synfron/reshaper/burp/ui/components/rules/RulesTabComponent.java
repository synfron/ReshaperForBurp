package synfron.reshaper.burp.ui.components.rules;

import javax.swing.*;
import java.awt.*;

public class RulesTabComponent extends JPanel {

    public RulesTabComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        RuleListComponent ruleList = new RuleListComponent();
        RuleContainerComponent ruleContainer = new RuleContainerComponent();
        ruleList.setSelectionContainer(ruleContainer);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ruleList, ruleContainer);

        add(splitPane);
    }
}
