package synfron.reshaper.burp.ui.components.rules;

import synfron.reshaper.burp.ui.models.rules.RuleModel;

import javax.swing.*;
import java.awt.*;

public class RuleContainerComponent extends JPanel {

    public RuleContainerComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
    }

    public void setModel(RuleModel model) {
        removeAll();
        if (model != null) {
            add(new RuleComponent(model));
        }
        revalidate();
        repaint();
    }
}
