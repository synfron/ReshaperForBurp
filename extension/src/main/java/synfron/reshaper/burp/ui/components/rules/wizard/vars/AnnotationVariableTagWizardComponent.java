package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.MessageAnnotation;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.AnnotationVariableTagWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AnnotationVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final AnnotationVariableTagWizardModel model;
    private JComboBox<MessageAnnotation> messageAnnotation;

    public AnnotationVariableTagWizardComponent(AnnotationVariableTagWizardModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        messageAnnotation = createComboBox(MessageAnnotation.values());

        messageAnnotation.setSelectedItem(model.getMessageAnnotation());

        messageAnnotation.addActionListener(this::onMessageAnnotationChanged);

        add(getLabeledField("Annotation", messageAnnotation), "wrap");
    }

    private void onMessageAnnotationChanged(ActionEvent actionEvent) {
        model.setMessageAnnotation((MessageAnnotation)messageAnnotation.getSelectedItem());
    }
}
