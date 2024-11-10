package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.VariableTagWizardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class VariableTagWizardComponent extends JPanel implements IFormComponent {

    private final VariableTagWizardModel model;
    private JComboBox<VariableSource> variableSource;
    private final VariableTagWizardContainerComponent container;
    private final ProtocolType protocolType;
    private final Consumer<Boolean> fieldUpdater;
    private final IEventListener<PropertyChangedArgs> modelChangedListener = this::onModelChanged;

    public VariableTagWizardComponent(VariableTagWizardModel model, ProtocolType protocolType, Consumer<Boolean> fieldUpdater) {
        this.model = model;
        container = new VariableTagWizardContainerComponent(protocolType);
        this.protocolType = protocolType;
        this.fieldUpdater = fieldUpdater;
        initComponent();

        model.withListener(modelChangedListener);
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        variableSource = createComboBox(VariableSource.getAll(protocolType));

        variableSource.setSelectedItem(model.getVariableSource());

        variableSource.addActionListener(this::onVariableSourceChanged);

        add(getLabeledField("Tag Type", variableSource), BorderLayout.PAGE_START);
        add(container, BorderLayout.CENTER);

        container.setModel(model.getTagModel());
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        switch (propertyChangedArgs.getName()) {
            case "invalidated": {
                if (model.isInvalidated()) {
                    JOptionPane.showMessageDialog(this,
                            String.join("\n", model.validate()),
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "dismissed": {
                if (model.isDismissed()) {
                    fieldUpdater.accept(!model.isInvalidated());
                }
            }
            case "tagModel": {
                container.setModel(model.getTagModel());
            }
        }
    }

    private void onVariableSourceChanged(ActionEvent actionEvent) {
        model.setVariableSource((VariableSource)variableSource.getSelectedItem());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
