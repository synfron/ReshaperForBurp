package synfron.reshaper.burp.ui.components.vars;

import synfron.reshaper.burp.ui.models.vars.VariableModel;

import javax.swing.*;
import java.awt.*;

public class VariableContainerComponent extends JPanel {

    public VariableContainerComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
    }

    public void setModel(VariableModel model) {
        removeAll();
        if (model != null) {
            add(new VariableComponent(model));
        }
        revalidate();
        repaint();
    }
}
