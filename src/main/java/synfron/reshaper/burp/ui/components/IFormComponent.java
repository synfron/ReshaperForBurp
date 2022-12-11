package synfron.reshaper.burp.ui.components;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.components.rules.RuleOperationComponent;
import synfron.reshaper.burp.ui.components.rules.wizard.vars.VariableTagWizardOptionPane;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.VariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ActionPerformedListener;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;

public interface IFormComponent {

    default Component getLabeledField(String label, Component innerComponent) {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout());
        container.setBorder(null);

        if (innerComponent instanceof JTextField || innerComponent instanceof JComboBox<?>) {
            container.setBorder(BorderFactory.createEmptyBorder(0, -3, 0, 0));
        }

        container.add(new JLabel(label), "wrap");
        container.add(innerComponent);
        return container;
    }

    default <T> JComboBox<T> createComboBox(T[] array) {
        return createComboBox(array, false);
    }

    default <T> JComboBox<T> createComboBox(T[] array, boolean isEditable) {
        JComboBox<T> comboBox = new JComboBox<>(array);
        if (isEditable) {
            comboBox.setEditable(true);
            int columnSize = comboBox.getFontMetrics(comboBox.getFont()).charWidth('m') * 20;
            comboBox.setPreferredSize(new Dimension(columnSize, comboBox.getPreferredSize().height));
        }
        return comboBox;
    }

    private ProtocolType getProtocolType() {
        return this instanceof RuleOperationComponent<?, ?> ? ((RuleOperationComponent<?, ?>)this).getProtocolType() : ProtocolType.Any;
    }

    default JTextField createTextField(boolean supportsVariableTags) {
        JTextField textField = new JTextField();
        textField.setColumns(20);
        textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, textField.getPreferredSize().height));
        textField.setAlignmentX(LEFT_ALIGNMENT);
        return addContextMenu(addUndo(textField), supportsVariableTags, getProtocolType());
    }

    default Component getPaddedButton(JButton button) {
        JPanel outerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerContainer.setAlignmentX(LEFT_ALIGNMENT);
        outerContainer.setAlignmentY(TOP_ALIGNMENT);
        outerContainer.add(button);
        return outerContainer;
    }

    default JTextPane createTextPane() {
        return addContextMenu(addUndo(new JTextPane()), false, getProtocolType());
    }

    private static <T extends JTextComponent> T addContextMenu(T textComponent, boolean supportsVariableTags, ProtocolType protocolType) {
        JPopupMenu popupMenu = new JPopupMenu();

        Action cut = new DefaultEditorKit.CutAction();
        Action copy = new DefaultEditorKit.CopyAction();
        Action paste = new DefaultEditorKit.PasteAction();
        Action selectAll = new ActionPerformedListener(event -> textComponent.selectAll());

        cut.putValue(Action.NAME, "Cut");
        copy.putValue(Action.NAME, "Copy");
        paste.putValue(Action.NAME, "Paste");
        selectAll.putValue(Action.NAME, "Select All");

        popupMenu.add(cut);
        popupMenu.add(copy);
        popupMenu.add(paste);
        popupMenu.add(selectAll);

        if (supportsVariableTags) {
            Action addVariableTag =  new ActionPerformedListener(event -> insertVariableTag(textComponent, protocolType));
            addVariableTag.putValue(Action.NAME, "Insert Variable Tag");
            popupMenu.addSeparator();
            popupMenu.add(addVariableTag);
        }

        textComponent.setComponentPopupMenu(popupMenu);

        return textComponent;
    }

    private static <T extends JTextComponent> void insertVariableTag(T textComponent, ProtocolType protocolType) {
        VariableTagWizardModel model = new VariableTagWizardModel();
        do {
            ModalPrompter.open(model, ignored -> VariableTagWizardOptionPane.showDialog(model, protocolType), false);
        } while (model.isInvalidated() && !model.isDismissed());
        if (!model.isDismissed()) {
            String tag = model.getTag();
            if (StringUtils.isNotEmpty(tag)) {
                int cursorPosition = textComponent.getCaretPosition();
                int insertPosition = VariableString.getVariableTagPositions(textComponent.getText()).stream()
                        .noneMatch(position -> position.getLeft() < cursorPosition && cursorPosition < position.getRight()) ?
                        cursorPosition :
                        textComponent.getText().length();
                StringBuilder text = new StringBuilder(textComponent.getText());
                text.insert(insertPosition, tag);
                textComponent.setText(text.toString());
                textComponent.setCaretPosition(insertPosition + tag.length());
            }
        }
        textComponent.requestFocus();
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
