package synfron.reshaper.burp.ui.components.vars;

import burp.BurpExtender;
import burp.api.montoya.ui.editor.Editor;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.utils.BurpUtils;
import synfron.reshaper.burp.core.vars.VariableValueType;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.vars.VariableModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;
import synfron.reshaper.burp.ui.utils.DocumentListenerFinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class VariableComponent extends JPanel implements IFormComponent {
    private final VariableModel model;
    @Getter
    private final Workspace workspace;
    private JTextField variableName;
    private Editor variableText;
    private JComboBox<VariableValueType> valueType;
    private JTextField delimiter;
    private JCheckBox persistent;
    private JButton save;
    private final IEventListener<PropertyChangedArgs> variablePropertyChangedListener = this::onVariablePropertyChanged;
    private JPanel variableTextContainer;

    public VariableComponent(VariableModel model) {
        this.workspace = getHostedWorkspace();
        this.model = model;
        model.withListener(variablePropertyChangedListener);
        initComponent();
    }

    private void setEditorVariableText(String text) {
        switch (model.getValueType()) {
            case Text -> ((WebSocketMessageEditor)variableText).setContents(BurpUtils.current.toByteArray(text));
            case Request -> ((HttpRequestEditor)variableText).setRequest(BurpUtils.current.toHttpRequest(text));
            case Response -> ((HttpResponseEditor)variableText).setResponse(BurpUtils.current.toHttpResponse(text));
        }
    }

    private String getVariableText() {
        return switch (model.getValueType()) {
            case Text -> ((WebSocketMessageEditor) variableText).getContents().toString();
            case Request -> ((HttpRequestEditor) variableText).getRequest().toString();
            case Response -> ((HttpResponseEditor) variableText).getResponse().toString();
        };
    }

    private void onVariablePropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if ("this".equals(propertyChangedArgs.getName())) {
            if (!Objects.equals(model.getValue(), getVariableText())) {
                setEditorVariableText(model.getValue());
            }
            if (model.isList()) {
                delimiter.setText(model.getDelimiter());
            }
            persistent.setSelected(model.isPersistent());
        } else if ("saved".equals(propertyChangedArgs.getName())) {
            setSaveButtonState();
        } else if ("valueType".equals(propertyChangedArgs.getName())) {
            recreateVariableText();
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

        variableName = createTextField(false);
        valueType = createComboBox(VariableValueType.values());

        variableName.setText(model.getName());
        valueType.setSelectedItem(model.getValueType());

        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));
        valueType.addActionListener(this::onValueTypeChanged);

        container.add(getLabeledField("Variable Name *", variableName));
        container.add(getLabeledField("Value Type", valueType));

        if (model.isList()) {
            delimiter = createTextField(false);
            delimiter.setText(model.getDelimiter());
            delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
            container.add(getLabeledField("Delimiter *", delimiter));
        }
        return container;
    }

    private Component getVariableTextBox() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        variableTextContainer = new JPanel(new BorderLayout());

        recreateVariableText();

        container.add(new JLabel("Variable Text"), BorderLayout.PAGE_START);
        container.add(variableTextContainer, BorderLayout.CENTER);
        return container;
    }

    private void recreateVariableText() {
        switch (model.getValueType()) {
            case Text -> variableText = BurpExtender.getApi().userInterface().createWebSocketMessageEditor();
            case Request -> variableText = BurpExtender.getApi().userInterface().createHttpRequestEditor();
            case Response -> variableText = BurpExtender.getApi().userInterface().createHttpResponseEditor();
        }
        setEditorVariableText(model.getValue());

        variableTextContainer.removeAll();
        variableTextContainer.add(variableText.uiComponent());

        new DocumentListenerFinder(variableText.uiComponent(), new DocumentActionListener(this::onVariableTextChanged));
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onValueTypeChanged(ActionEvent actionEvent) {
        model.setValueType((VariableValueType) valueType.getSelectedItem());
    }

    private void onVariableTextChanged(ActionEvent actionEvent) {
        createInvokeLaterEntryPoint(() -> {
            if (!model.isSaved() || !Objects.equals(getVariableText(), model.getValue())) {
                model.setValue(getVariableText());
            }
        });
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

    @SuppressWarnings("unchecked")
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

    private void onPersistentChanged(ActionEvent actionEvent) {
        model.setPersistent(persistent.isSelected());
    }

    private void onSave(ActionEvent actionEvent) {
        model.setName(variableName.getText());
        model.setValue(getVariableText());
        model.setPersistent(persistent.isSelected());
        if (!model.save()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
