package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IUuidGeneratorModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UuidGeneratorComponent extends GeneratorComponent<IUuidGeneratorModel> {
    
    private JComboBox<ValueGenerator.UuidVersion> version;
    private JTextField namespace;
    private JTextField name;

    public UuidGeneratorComponent(IUuidGeneratorModel model, boolean allowVariableTags) {
        super(model, allowVariableTags);
    }

    protected void initComponent() {
        version = createComboBox(ValueGenerator.UuidVersion.values());
        namespace = createTextField(allowVariableTags);
        name = createTextField(allowVariableTags);

        version.setSelectedItem(model.getVersion());
        namespace.setText(model.getNamespace());
        name.setText(model.getName());

        version.addActionListener(this::onVersionChanged);
        namespace.getDocument().addDocumentListener(new DocumentActionListener(this::onNamespaceChanged));
        name.getDocument().addDocumentListener(new DocumentActionListener(this::onNameChanged));

        add(getLabeledField("Version", version), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Namespace (UUID) *", namespace),
                version,
                () -> ((ValueGenerator.UuidVersion)version.getSelectedItem()).isHasInputs()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Name *", name),
                version,
                () -> ((ValueGenerator.UuidVersion)version.getSelectedItem()).isHasInputs()
        ), "wrap");
    }

    private void onVersionChanged(ActionEvent actionEvent) {
        model.setVersion((ValueGenerator.UuidVersion)version.getSelectedItem());
    }

    private void onNamespaceChanged(ActionEvent actionEvent) {
        model.setNamespace(namespace.getText());
    }

    private void onNameChanged(ActionEvent actionEvent) {
        model.setName(name.getText());
    }
}
