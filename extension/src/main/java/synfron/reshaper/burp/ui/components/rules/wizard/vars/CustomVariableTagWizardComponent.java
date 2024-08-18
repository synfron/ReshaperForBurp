package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.CustomVariableTagWizardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class CustomVariableTagWizardComponent<T extends CustomVariableTagWizardModel> extends JPanel implements IFormComponent {
    protected final T model;
    private JComboBox<String> variableName;

    public CustomVariableTagWizardComponent(T model) {
        this.model = model;
        initComponent();
    }

    protected void initComponent() {
        setLayout(new MigLayout());

        variableName = createComboBox(model.getVariableNames().toArray(new String[0]), true);

        variableName.setSelectedItem(model.getVariableNames().stream().findFirst().orElse(""));

        variableName.addActionListener(this::onVariableNameChanged);

        add(getLabeledField("Variable Name *", variableName), "wrap");
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName((String)variableName.getSelectedItem());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
