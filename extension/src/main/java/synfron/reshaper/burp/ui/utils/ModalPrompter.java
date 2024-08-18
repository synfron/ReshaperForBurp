package synfron.reshaper.burp.ui.utils;

import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.IFormComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ModalPrompter<T extends IPrompterModel<T>> {
    private static final Map<String, JDialog> dialogMap = new ConcurrentHashMap<>();
    private final T model;
    private final IPrompter<T> prompter;
    private final boolean reopenOnError;

    private final IEventListener<PropertyChangedArgs> modelChangedListener = this::onModelChanged;

    public ModalPrompter(T model, IPrompter<T> prompter, boolean reopenOnError) {
        this.model = model;
        this.prompter = prompter;
        this.reopenOnError = reopenOnError;

        model.setModalPrompter(this);
    }

    private void open() {
        model.resetPropertyChangedListener();
        model.withListener(modelChangedListener);
        prompter.open(model);
    }

    public static <T extends IPrompterModel<T>> void open(T model, IPrompter<T> prompter, boolean reopenOnError) {
        ModalPrompter<T> modalPrompter = new ModalPrompter<>(model, prompter, reopenOnError);
        modalPrompter.open();
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        if (reopenOnError && !model.isDismissed() && propertyChangedArgs.getName().equals("invalidated") && (boolean)propertyChangedArgs.getValue()) {
            ModalPrompter.open(model, prompter, reopenOnError);
        }
    }

    public static void createTextAreaDialog(String id, String title, String description, String text, Consumer<String> valueHandler) {
        JPanel container = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new EmptyBorder(10,0,0,0));
        JTextPane inputText = IFormComponent.addUndo(new JTextPane());
        inputText.setText(text);
        scrollPane.setViewportView(inputText);
        scrollPane.setPreferredSize(new Dimension(320, 160));

        container.add(new JLabel(description), BorderLayout.PAGE_START);
        container.add(scrollPane, BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(container, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[]{ "OK", "Cancel" }, "OK");
        JDialog dialog = optionPane.createDialog(title);
        dialog.setResizable(true);
        dialogMap.put(id, dialog);

        optionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, event -> {
            if (Objects.equals(optionPane.getValue(), "OK")) {
                valueHandler.accept(inputText.getText());
            } else {
                valueHandler.accept(null);
            }
            dismiss(id);
        });

        dialog.setModal(false);
        dialog.setVisible(true);
    }

    public static void dismiss(String id) {
        JDialog dialog = dialogMap.get(id);
        if (dialog != null) {
            dialog.dispose();
            dialogMap.remove(id);
        }
    }
}
