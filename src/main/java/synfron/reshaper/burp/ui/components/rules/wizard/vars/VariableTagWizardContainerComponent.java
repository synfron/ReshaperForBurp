package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import synfron.reshaper.burp.ui.models.rules.wizard.vars.*;

import javax.swing.*;
import java.awt.*;

public class VariableTagWizardContainerComponent extends JPanel {

    public VariableTagWizardContainerComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
    }

    public void setModel(IVariableTagWizardModel model) {
        removeAll();
        if (model != null) {
            JPanel component = switch (model.getVariableSource()) {
                case Event -> new EventVariableTagWizardComponent((EventVariableTagWizardModel) model);
                case Global -> new GlobalVariableTagWizardComponent((GlobalVariableTagWizardModel) model);
                case Message -> new MessageVariableTagWizardComponent((MessageVariableTagWizardModel) model);
                case File -> new FileVariableTagWizardComponent((FileVariableTagWizardModel) model);
                case Special -> new SpecialVariableTagWizardComponent((SpecialVariableTagWizardModel) model);
                case CookieJar -> new CookieJarVariableTagWizardComponent((CookieJarVariableTagWizardModel) model);
            };
            add(component);
        }
        revalidate();
        repaint();
    }
}
