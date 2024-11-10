package synfron.reshaper.burp.ui.components.shared;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.components.rules.RuleOperationComponent;
import synfron.reshaper.burp.ui.components.rules.wizard.vars.VariableTagWizardComponent;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceDependentComponent;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceHost;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.VariableTagWizardModel;
import synfron.reshaper.burp.ui.utils.ActionPerformedListener;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;

public interface IFormComponent extends IWorkspaceDependentComponent, IWorkspaceHost {

    @Override
    default Workspace getWorkspace() {
        return getHostedWorkspace(getComponent());
    }

    <T extends Component & IFormComponent> T getComponent();

    default Workspace getHostedWorkspace() {
        return getHostedWorkspace(getComponent());
    }

    default JPanel getLabeledField(String label, Component innerComponent) {
        return getLabeledField(label, innerComponent, true);
    }

    default JPanel getLabeledField(String label, Component innerComponent, boolean span) {
        return getLabeledField(new JLabel(label), innerComponent, span);
    }

    default JPanel getLabeledField(JLabel label, Component innerComponent) {
        return getLabeledField(label, innerComponent, true);
    }

    default JPanel getLabeledField(JLabel label, Component innerComponent, boolean span) {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout());
        container.setBorder(null);

        if (innerComponent instanceof JTextField || innerComponent instanceof JComboBox<?>) {
            container.setBorder(BorderFactory.createEmptyBorder(0, -3, 0, 0));
        }

        container.add(label, "wrap");
        container.add(innerComponent, span ? "grow, push, span" : "");
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

    default <T> JComboBox<T> createComboBox(ComboBoxModel<T> model) {
        return createComboBox(model, false);
    }

    default <T> JComboBox<T> createComboBox(ComboBoxModel<T> model, boolean isEditable) {
        JComboBox<T> comboBox = new JComboBox<>(model);
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

    private <T extends JTextField> T createTextField(T textField, boolean supportsVariableTags) {
        textField.setColumns(20);
        textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, textField.getPreferredSize().height));
        textField.setAlignmentX(LEFT_ALIGNMENT);
        return addContextMenu(addUndo(textField), supportsVariableTags, getProtocolType());
    }

    default PromptTextField createPromptTextField(String placeholder, boolean supportsVariableTags) {
        PromptTextField textField = new PromptTextField(placeholder);
        textField.getTextPrompt().setShow(TextPrompt.Show.FOCUS_LOST);
        textField.getTextPrompt().changeStyle(Font.ITALIC);
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

    default <T extends JTextComponent> T createTextComponent(T textComponent) {
        return addContextMenu(addUndo(textComponent), false, getProtocolType());
    }

    private <T extends JTextComponent> T addContextMenu(T textComponent, boolean supportsVariableTags, ProtocolType protocolType) {
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
            Action addVariableTag =  new ActionPerformedListener(event -> createEntryPoint(() -> insertVariableTag(textComponent, protocolType)));
            addVariableTag.putValue(Action.NAME, "Insert Variable Tag");
            popupMenu.addSeparator();
            popupMenu.add(addVariableTag);
        }

        textComponent.setComponentPopupMenu(popupMenu);

        return textComponent;
    }

    private static <T extends JTextComponent> void insertVariableTag(T textComponent, ProtocolType protocolType) {
        VariableTagWizardModel model = new VariableTagWizardModel();

        Consumer<Boolean> fieldUpdater = valid -> {
            if (valid) {
                String tag = model.getTag();
                if (StringUtils.isNotEmpty(tag)) {
                    int cursorPosition = textComponent.getCaretPosition();
                    int insertPosition = VariableTag.getVariableTagPositions(textComponent.getText()).stream()
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
        };

        ModalPrompter.open(model, new ModalPrompter.FormPromptArgs<>(
            "Variable Tag",
            model,
            new VariableTagWizardComponent(model, protocolType, fieldUpdater)
        ).resizable(true));


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
