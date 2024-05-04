package synfron.reshaper.burp.ui.components.rules.thens.generate;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.utils.PasswordCharacterGroup;
import synfron.reshaper.burp.ui.models.rules.thens.generate.PasswordGeneratorModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PasswordGeneratorComponent extends GeneratorComponent<PasswordGeneratorModel> {

    private JTextField minLength;
    private JTextField maxLength;

    public PasswordGeneratorComponent(PasswordGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        minLength = createTextField(true);
        maxLength = createTextField(true);

        minLength.setText(model.getMinLength());
        maxLength.setText(model.getMaxLength());

        minLength.getDocument().addDocumentListener(new DocumentActionListener(this::onMinLengthChanged));
        maxLength.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxLengthChanged));

        add(getLabeledField("Min Length *", minLength), "wrap");
        add(getLabeledField("Max Length *", maxLength), "wrap");
        add(getCharacterGroupOptions(), "wrap");
    }

    private Component getCharacterGroupOptions() {
        JPanel container = new JPanel(new MigLayout());

        container.add(new JLabel("Character Groups *:"), "wrap");
        for (PasswordCharacterGroup group : PasswordCharacterGroup.values()) {
            JCheckBox checkbox = new JCheckBox(group.toString());
            checkbox.putClientProperty("item", group);

            checkbox.setSelected(model.getCharacterGroups().contains(group));

            checkbox.addActionListener(this::onPasswordGroupCheckboxChanged);

            container.add(checkbox, "wrap");
        }

        return container;
    }

    private void onPasswordGroupCheckboxChanged(ActionEvent actionEvent) {
        JCheckBox checkbox = (JCheckBox)actionEvent.getSource();
        PasswordCharacterGroup group = (PasswordCharacterGroup) checkbox.getClientProperty("item");
        if (checkbox.isSelected()) {
            model.addPasswordCharacterGroups(group);
        } else {
            model.removePasswordCharacterGroups(group);
        }
    }

    private void onMinLengthChanged(ActionEvent actionEvent) {
        model.setMinLength(minLength.getText());
    }

    private void onMaxLengthChanged(ActionEvent actionEvent) {
        model.setMaxLength(maxLength.getText());
    }
}
