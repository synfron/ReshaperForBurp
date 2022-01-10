package synfron.reshaper.burp.ui.components;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.utils.ActionPerformedListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public interface IFormComponent {

    default Component getLabeledField(String label, Component innerComponent) {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout());
        container.setBorder(null);

        if (innerComponent instanceof JTextField) {
            JTextField textField = (JTextField) innerComponent;
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

    default JTextField createTextField() {
        return addUndo(new JTextField());
    }

    default JTextPane createTextPane() {
        return addUndo(new JTextPane());
    }
    
    static <T extends JTextComponent> T addUndo(T textComponent) {
        UndoManager undoManager = new UndoManager();

        textComponent.getDocument().addUndoableEditListener(event -> undoManager.addEdit(event.getEdit()));
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK);

        textComponent.getInputMap().put(undoKeyStroke, "undo");
        textComponent.getInputMap().put(redoKeyStroke, "redo");

        textComponent.getActionMap().put("undo", new ActionPerformedListener(event -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        }));

        textComponent.getActionMap().put("redo", new ActionPerformedListener(event -> {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        }));
        return textComponent;
    }
}
