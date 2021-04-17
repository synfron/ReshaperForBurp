package synfron.reshaper.burp.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public interface IFormComponent {

    default Component getLabeledField(String label, Component innerComponent) {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout());
        container.setBorder(null);

        if (innerComponent instanceof JTextField) {
            JTextField textField = (JTextField)innerComponent;
            textField.setColumns(20);
            textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, textField.getPreferredSize().height));
            textField.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.setBorder(BorderFactory.createEmptyBorder(0, -3, 0, 0));
        } else if (innerComponent instanceof JComboBox<?>) {
            container.setBorder(BorderFactory.createEmptyBorder(0, -3, 0, 0));
        }

        container.add(new JLabel(label), "wrap");
        container.add(innerComponent);
        return container;
    }
}
