package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.*;

import javax.swing.*;
import java.awt.*;

public class VariableTagWizardContainerComponent extends JPanel {

    private final ProtocolType protocolType;

    public VariableTagWizardContainerComponent(ProtocolType protocolType) {
        this.protocolType = protocolType;
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
                case Session -> new SessionVariableTagWizardComponent((SessionVariableTagWizardModel) model);
                case EventList -> new EventListVariableTagWizardComponent((EventListVariableTagWizardModel) model);
                case GlobalList -> new GlobalListVariableTagWizardComponent((GlobalListVariableTagWizardModel) model);
                case SessionList -> new SessionListVariableTagWizardComponent((SessionListVariableTagWizardModel) model);
                case Message -> new MessageVariableTagWizardComponent((MessageVariableTagWizardModel) model, protocolType);
                case Annotation -> new AnnotationVariableTagWizardComponent((AnnotationVariableTagWizardModel) model);
                case File -> new FileVariableTagWizardComponent((FileVariableTagWizardModel) model);
                case Special -> new SpecialVariableTagWizardComponent((SpecialVariableTagWizardModel) model);
                case CookieJar -> new CookieJarVariableTagWizardComponent((CookieJarVariableTagWizardModel) model);
                case Macro -> new MacroVariableTagWizardComponent((MacroVariableTagWizardModel) model, protocolType);
                case Generator -> new GeneratorVariableTagWizardComponent((GeneratorVariableTagWizardModel) model);
            };
            add(component);
        }
        revalidate();
        repaint();
    }
}
