package synfron.reshaper.burp.ui.components.rules;

import lombok.SneakyThrows;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.Map;

public class RuleComponent extends JPanel {
    private final RuleModel model;
    private JCheckBox isEnabled;
    private JCheckBox autoRun;
    private JTextField ruleName;
    private JButton save;

    public RuleComponent(RuleModel model) {
        this.model = model;

        model.getPropertyChangedEvent().add(this::OnModelPropertyChanged);

        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        add(getRuleNameBox(), BorderLayout.PAGE_START);
        add(getRuleOperations(), BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private Component getRuleOperations() {
        return new RuleOperationsContainerComponent(model);
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

    private Component getRuleNameBox() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        ruleName = new JTextField();

        ruleName.setText(model.getName());
        ruleName.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        ruleName.setAlignmentX(Component.LEFT_ALIGNMENT);
        ruleName.setColumns(20);
        ruleName.setMaximumSize(ruleName.getPreferredSize());

        ruleName.getDocument().addDocumentListener(new DocumentActionListener(this::onRuleNameChanged));

        container.add(new JLabel("Rule Name"));
        container.add(ruleName);
        return container;
    }

    private Component getGitHubLink() {
        JLabel githubLink = new JLabel("View on GitHub");
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
                Desktop.getDesktop().browse(new URI("https://github.com/synfron/ReshaperForBurp"));
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

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new BorderLayout());

        JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        autoRun = new JCheckBox("Auto Run");
        isEnabled = new JCheckBox("Enabled");
        save = new JButton("Save");
        setSaveButtonState();

        autoRun.setSelected(model.isAutoRun());
        isEnabled.setSelected(model.isEnabled());

        autoRun.addActionListener(this::onAutoRun);
        isEnabled.addActionListener(this::onEnabled);
        save.addActionListener(this::onSave);

        buttonSection.add(autoRun);
        buttonSection.add(isEnabled);
        buttonSection.add(save);


        actionBar.add(getGitHubLink(), BorderLayout.LINE_START);
        actionBar.add(buttonSection, BorderLayout.LINE_END);

        return actionBar;
    }

    private void OnModelPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if ("saved".equals(propertyChangedArgs.getName())) {
            setSaveButtonState();
        }
    }

    private void onAutoRun(ActionEvent actionEvent) {
        model.setAutoRun(autoRun.isSelected());
    }

    private void onRuleNameChanged(ActionEvent actionEvent) {
        model.setName(ruleName.getText());
    }

    private void onEnabled(ActionEvent actionEvent) {
        model.setEnabled(isEnabled.isSelected());
    }

    private void onSave(ActionEvent actionEvent) {
        if (!model.persist()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
