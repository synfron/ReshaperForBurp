package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.rules.thens.entities.generate.UuidGenerator;
import synfron.reshaper.burp.ui.models.rules.thens.generate.UuidGeneratorModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UuidGeneratorComponent extends GeneratorComponent<UuidGeneratorModel> {
    
    private JComboBox<UuidGenerator.UuidVersion> version;
    private JTextField namespace;
    private JTextField name;

    public UuidGeneratorComponent(UuidGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        version = createComboBox(UuidGenerator.UuidVersion.values());
        namespace = createTextField(true);
        name = createTextField(true);

        version.setSelectedItem(model.getVersion());
        namespace.setText(model.getNamespace());
        name.setText(model.getName());

        version.addActionListener(this::onVersionChanged);
        namespace.getDocument().addDocumentListener(new DocumentActionListener(this::onNamespaceChanged));
        name.getDocument().addDocumentListener(new DocumentActionListener(this::onNameChanged));

        add(getLabeledField("Version", version), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Namespace *", namespace),
                version,
                () -> ((UuidGenerator.UuidVersion)version.getSelectedItem()).isHasInputs()
        ), "wrap");
        add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Name *", name),
                version,
                () -> ((UuidGenerator.UuidVersion)version.getSelectedItem()).isHasInputs()
        ), "wrap");
    }

    private void onVersionChanged(ActionEvent actionEvent) {
        model.setVersion((UuidGenerator.UuidVersion)version.getSelectedItem());
    }

    private void onNamespaceChanged(ActionEvent actionEvent) {
        model.setNamespace(namespace.getText());
    }

    private void onNameChanged(ActionEvent actionEvent) {
        model.setName(name.getText());
    }
}
