package synfron.reshaper.burp.ui.components.vars;

import lombok.SneakyThrows;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.models.vars.VariableModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.Map;

public class VariableComponent extends JPanel {
    private final VariableModel model;
    private JTextField variableName;
    private JTextPane variableText;
    private JCheckBox persistent;
    private JButton save;
    private final IEventListener<PropertyChangedArgs> variablePropertyChangedListener = this::onVariablePropertyChanged;

    public VariableComponent(VariableModel model) {
        this.model = model;
        model.withListener(variablePropertyChangedListener);
        initComponent();
    }

    private void onVariablePropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if ("this".equals(propertyChangedArgs.getName())) {
            variableText.setText(model.getValue());
            persistent.setSelected(model.isPersistent());
        } else if ("saved".equals(propertyChangedArgs.getName())) {
            setSaveButtonState();
        }
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        add(getVariableNameBox(), BorderLayout.PAGE_START);
        add(getVariableTextBox(), BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private void setSaveButtonState() {
        if (model.isSaved()) {
            save.setEnabled(false);
            save.setText("Saved");
        } else {
            save.setEnabled(true);
            save.setText("Save");
        }
    }

    private Component getVariableNameBox() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        variableName = new JTextField();
        variableName.setText(model.getName());
        variableName.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        variableName.setAlignmentX(Component.LEFT_ALIGNMENT);
        variableName.setColumns(20);
        variableName.setMaximumSize(variableName.getPreferredSize());

        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));

        container.add(new JLabel("Variable Name"));
        container.add(variableName);
        return container;
    }

    private Component getVariableTextBox() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        variableText = new JTextPane();
        variableText.setText(model.getValue());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.setViewportView(variableText);

        variableText.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableTextChanged));

        container.add(new JLabel("Variable Text"), BorderLayout.PAGE_START);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void onVariableTextChanged(ActionEvent actionEvent) {
        model.setValue(variableText.getText());
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new BorderLayout());

        JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        persistent = new JCheckBox("Persistent");
        persistent.setSelected(model.isPersistent());
        save = new JButton("Save");
        setSaveButtonState();

        persistent.addActionListener(this::onPersistentChanged);
        save.addActionListener(this::onSave);

        buttonSection.add(persistent);
        buttonSection.add(save);

        actionBar.add(getGitHubLink(), BorderLayout.LINE_START);
        actionBar.add(buttonSection, BorderLayout.LINE_END);

        return actionBar;
    }

    private Component getGitHubLink() {
        JLabel githubLink = new JLabel("Help");
        githubLink.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        Font font = githubLink.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        githubLink.setFont(font.deriveFont(attributes));

        githubLink.addMouseListener(new MouseListener() {
            private Color originalColor = githubLink.getForeground();

            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop.getDesktop().browse(new URI("https://synfron.github.io/ReshaperForBurp"));
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                githubLink.setForeground(new Color(0, 0, 0xC0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                githubLink.setForeground(originalColor);
            }
        });

        return githubLink;
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setName(variableName.getText());
    }

    private void onPersistentChanged(ActionEvent actionEvent) {
        model.setPersistent(persistent.isSelected());
    }

    private void onSave(ActionEvent actionEvent) {
        model.setName(variableName.getText());
        model.setValue(variableText.getText());
        model.setPersistent(persistent.isSelected());
        if (!model.save()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
