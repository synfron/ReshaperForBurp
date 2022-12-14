package synfron.reshaper.burp.ui.components.rules;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.models.rules.RuleModel;

import javax.swing.*;
import java.awt.*;

public class RuleContainerComponent extends JPanel {

    private final ProtocolType protocolType;

    public RuleContainerComponent(ProtocolType protocolType) {
        this.protocolType = protocolType;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
    }

    public void setModel(RuleModel model) {
        removeAll();
        if (model != null) {
            add(new RuleComponent(protocolType, model));
        }
        revalidate();
        repaint();
    }
}
