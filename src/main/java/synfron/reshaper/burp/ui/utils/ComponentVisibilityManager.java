package synfron.reshaper.burp.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ComponentVisibilityManager {

    public static Component withVisibilityFieldChangeDependency(
            Component dependent,
            Component dependee,
            BooleanSupplier condition
    ) {
        return withVisibilityFieldChangeDependency(dependent, List.of(dependee), condition);
    }

    public static Component withVisibilityFieldChangeDependency(
            Component dependent,
            List<Component> dependees,
            BooleanSupplier condition
    ) {
        JPanel container = new JPanel();
        ((FlowLayout)container.getLayout()).setHgap(0);
        ((FlowLayout)container.getLayout()).setVgap(0);
        if (dependent.isEnabled()) {
            container.add(dependent);
        }
        dependees.forEach(dependee -> {
            if (dependee instanceof JTextField) {
                ((JTextField) dependee).getDocument().addDocumentListener(new DocumentActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition)));
            } else if (dependee instanceof JCheckBox) {
                ((JCheckBox) dependee).addActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition));
            } else if (dependee instanceof JComboBox) {
                ((JComboBox<?>) dependee).addActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition));
            }
        });
        updateVisibilityFieldChangeDependency(container, dependent, condition);
        return container;
    }

    private static void updateVisibilityFieldChangeDependency(JPanel container, Component dependent, BooleanSupplier condition) {
        boolean isVisible = condition.getAsBoolean();
        if (isVisible != dependent.isEnabled()) {
            if (isVisible) {
                container.add(dependent);
            } else {
                container.remove(dependent);
            }
            container.revalidate();
            container.repaint();
            dependent.setEnabled(isVisible);
        }
    }
}
