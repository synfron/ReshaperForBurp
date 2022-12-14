package synfron.reshaper.burp.ui.components.rules;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.components.rules.thens.ThensComponent;
import synfron.reshaper.burp.ui.components.rules.whens.WhensComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;

import javax.swing.*;
import java.awt.*;

public class RuleOperationsContainerComponent extends JPanel {
    private final ProtocolType protocolType;
    private final RuleModel model;

    public RuleOperationsContainerComponent(ProtocolType protocolType, RuleModel model) {
        this.protocolType = protocolType;
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new GridLayout(1, 2));

        add(getWhens());
        add(getThens());
    }

    private Component getWhens() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

        WhensComponent whens = new WhensComponent(protocolType, model);

        container.add(whens);
        return container;
    }

    private Component getThens() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

        ThensComponent thens = new ThensComponent(protocolType, model);

        container.add(thens);
        return container;
    }
}
