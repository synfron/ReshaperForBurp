package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.SessionVariableTagWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SessionVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final SessionVariableTagWizardModel model;
    private JComboBox<String> variableName;

    public SessionVariableTagWizardComponent(SessionVariableTagWizardModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        variableName = createComboBox(model.getVariableNames().toArray(new String[0]), true);

        variableName.setSelectedItem(model.getVariableNames().stream().findFirst().orElse(""));

        variableName.addActionListener(this::onVariableNameChanged);

        add(getLabeledField("Variable Name *", variableName));
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName((String)variableName.getSelectedItem());
    }
}
