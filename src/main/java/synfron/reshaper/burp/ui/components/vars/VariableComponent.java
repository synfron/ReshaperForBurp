package synfron.reshaper.burp.ui.components.vars;

import lombok.SneakyThrows;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.IFormComponent;
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

public class VariableComponent extends JPanel implements IFormComponent {
    private final VariableModel model;
    private JTextField variableName;
    private JTextPane variableText;
    private JTextField delimiter;
    private JCheckBox persistent;
    private JCheckBox removeCarriageReturnsOnSave;
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
            removeCarriageReturnsOnSave.setSelected(model.isRemoveCarriageReturnsOnSave());
            delimiter.setText(model.getDelimiter());
            persistent.setSelected(model.isPersistent());
        } else if ("saved".equals(propertyChangedArgs.getName())) {
            setSaveButtonState();
        }
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        add(getTopBar(), BorderLayout.PAGE_START);
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

    private Component getTopBar() {
        JPanel container = new JPanel(new MigLayout());
        container.add(getVariableNameBox(), "wrap");
        if (model.isList()) {
            delimiter = createTextField(false);
            delimiter.setText(model.getDelimiter());
            delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
            container.add(new JLabel("Delimiter *"), "wrap");
            container.add(delimiter);
        }
        return container;
    }

    private Component getVariableNameBox() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        variableName = createTextField(false);
        variableName.setText(model.getName());
        variableName.setAlignmentX(Component.LEFT_ALIGNMENT);
        variableName.setColumns(20);
        variableName.setMaximumSize(variableName.getPreferredSize());

        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));

        container.add(new JLabel("Variable Name *"));
        container.add(variableName);
        return container;
    }

    private Component getVariableTextBox() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        variableText = createTextPane();
        variableText.setText(model.getValue());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(variableText);

        variableText.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableTextChanged));

        container.add(new JLabel("Variable Text"), BorderLayout.PAGE_START);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onVariableTextChanged(ActionEvent actionEvent) {
        model.setValue(variableText.getText());
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new BorderLayout());

        JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        removeCarriageReturnsOnSave = new JCheckBox("Remove Carriage Returns on Save");
        persistent = new JCheckBox("Persistent");

        removeCarriageReturnsOnSave.setSelected(model.isRemoveCarriageReturnsOnSave());
        persistent.setSelected(model.isPersistent());
        save = new JButton("Save");
        setSaveButtonState();

        removeCarriageReturnsOnSave.addActionListener(this::onRemoveCarriageReturnsOnSaveChanged);
        persistent.addActionListener(this::onPersistentChanged);
        save.addActionListener(this::onSave);

        buttonSection.add(removeCarriageReturnsOnSave);
        buttonSection.add(persistent);
        buttonSection.add(save);

        actionBar.add(getGitHubLink(), BorderLayout.LINE_START);
        actionBar.add(buttonSection, BorderLayout.LINE_END);

        return actionBar;
    }

    private Component getGitHubLink() {
        JLabel githubLink = new JLabel("Help | View on GitHub");
        githubLink.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        Font font = githubLink.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        githubLink.setFont(font.deriveFont(attributes));

        githubLink.addMouseListener(new MouseListener() {
            private final Color originalColor = githubLink.getForeground();
            private Color hoverColor;

            private Color getHoverColor() {
                if (hoverColor == null) {
                    int halfByte = 128;
                    int newRed = (halfByte + originalColor.getRed()) / 2;
                    int newGreen = (halfByte + originalColor.getGreen()) / 2;
                    int newBlue = (halfByte + originalColor.getBlue()) / 2;
                    hoverColor = new Color(newRed, newGreen, newBlue);
                }
                return hoverColor;
            }

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
                githubLink.setForeground(getHoverColor());
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

    private void onRemoveCarriageReturnsOnSaveChanged(ActionEvent actionEvent) {
        model.setRemoveCarriageReturnsOnSave(removeCarriageReturnsOnSave.isSelected());
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
