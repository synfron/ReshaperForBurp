package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.VariableTagWizardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;

public class VariableTagWizardOptionPane extends JOptionPane implements IFormComponent {

    private final JPanel outerContainer;
    private final VariableTagWizardModel model;
    private JComboBox<VariableSource> variableSource;
    private final VariableTagWizardContainerComponent container;
    private JDialog currentDialog;
    private final ProtocolType protocolType;

    private VariableTagWizardOptionPane(VariableTagWizardModel model, ProtocolType protocolType) {
        super(new JPanel(new BorderLayout()), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        container = new VariableTagWizardContainerComponent(protocolType);
        outerContainer = (JPanel)message;
        this.model = model;
        this.protocolType = protocolType;
        addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this::onPropertyChanged);
        initComponent();

        model.withListener(this::onModelChanged);
    }

    private void initComponent() {
        variableSource = createComboBox(VariableSource.getAll(protocolType));

        variableSource.setSelectedItem(model.getVariableSource());

        variableSource.addActionListener(this::onVariableSourceChanged);

        outerContainer.add(getLabeledField("Tag Type", variableSource), BorderLayout.PAGE_START);
        outerContainer.add(container, BorderLayout.CENTER);

        container.setModel(model.getTagModel());

        container.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        switch (propertyChangedArgs.getName()) {
            case "tagModel" -> {
                container.setModel(model.getTagModel());
                resize();

            }
        }
    }

    private void resize() {
        if (currentDialog != null) {
            currentDialog.pack();
        }
    }

    private void onVariableSourceChanged(ActionEvent actionEvent) {
        model.setVariableSource((VariableSource)variableSource.getSelectedItem());
    }

    private void onPropertyChanged(PropertyChangeEvent event) {
        if (getValue() != null && (int)getValue() == JOptionPane.OK_OPTION) {
            if (!model.validate().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", model.validate()),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            model.setDismissed(true);
        }
    }

    public static void showDialog(VariableTagWizardModel model, ProtocolType protocolType) {
        VariableTagWizardOptionPane optionPane = new VariableTagWizardOptionPane(model, protocolType);
        JDialog dialog = optionPane.createDialog("Variable Tag");
        optionPane.setCurrentDialog(dialog);
        dialog.setVisible(true);
    }

    private void setCurrentDialog(JDialog dialog) {
        this.currentDialog = dialog;
    }
}
