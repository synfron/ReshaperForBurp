package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.SpecialVariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SpecialVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final SpecialVariableTagWizardModel model;
    private JComboBox<SpecialVariableTagWizardModel.SpecialChar> specialChar;
    private JTextField value;

    public SpecialVariableTagWizardComponent(SpecialVariableTagWizardModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        setLayout(new MigLayout());

        specialChar = createComboBox(model.getSpecialChars().toArray(new SpecialVariableTagWizardModel.SpecialChar[0]));
        value = createTextField(true);

        specialChar.setSelectedItem(model.getSpecialChar());
        value.setText(model.getValue());

        specialChar.addActionListener(this::onSpecialCharChanged);
        value.getDocument().addDocumentListener(new DocumentActionListener(this::onValueChanged));

        add(getLabeledField("Type", specialChar), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Value *", value),
                specialChar,
                () -> ((SpecialVariableTagWizardModel.SpecialChar)specialChar.getSelectedItem()).isValueRequired()
        ));
    }

    private void onSpecialCharChanged(ActionEvent actionEvent) {
        model.setSpecialChar((SpecialVariableTagWizardModel.SpecialChar) specialChar.getSelectedItem());
    }

    private void onValueChanged(ActionEvent actionEvent) {
        model.setValue(value.getText());
    }
}
