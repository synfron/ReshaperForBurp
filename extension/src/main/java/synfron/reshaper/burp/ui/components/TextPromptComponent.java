package synfron.reshaper.burp.ui.components;

import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.TextPromptModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TextPromptComponent extends JPanel implements IFormComponent {

    private final TextPromptModel model;
    private JTextPane inputText;

    public TextPromptComponent(TextPromptModel model) {
        setLayout(new BorderLayout());
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        add(getBody(), BorderLayout.CENTER);
    }

    private Component getBody() {
        JPanel container = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new EmptyBorder(10,0,0,0));
        
        inputText = IFormComponent.addUndo(new JTextPane());
        inputText.setText(model.getText());

        inputText.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        
        scrollPane.setViewportView(inputText);

        container.add(new JLabel(model.getDescription()), BorderLayout.PAGE_START);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(inputText.getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
