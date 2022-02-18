package synfron.reshaper.burp.ui.components.rules.wizard;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.WhenWizardItemModel;
import synfron.reshaper.burp.ui.models.rules.wizard.WizardMatchType;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class WhenWizardItemComponent extends JPanel implements IFormComponent {
    private final WhenWizardItemModel model;
    private final boolean deletable;
    private JComboBox<MessageValue> messageValue;
    private JComboBox<String> identifier;
    private JComboBox<WizardMatchType> matchType;
    private JTextField text;

    public WhenWizardItemComponent(WhenWizardItemModel model, boolean deletable) {
        this.model = model;
        this.deletable = deletable;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
        JPanel container = new JPanel(new MigLayout());

        messageValue = new JComboBox<>(MessageValue.values());
        identifier = new JComboBox<>(model.getIdentifiers().getOptions().toArray(new String[0]));
        matchType = new JComboBox<>(WizardMatchType.values());
        text = createTextField();
        text.setColumns(20);
        text.setMaximumSize(new Dimension(text.getPreferredSize().width, text.getPreferredSize().height));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setSelectedItem(model.getIdentifiers().getSelectedOption());
        matchType.setSelectedItem(model.getMatchType());
        text.setText(model.getText());

        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.addActionListener(this::onIdentifierChanged);
        matchType.addActionListener(this::onMatchTypeChanged);
        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));

        container.add(text, "wrap");
        container.add(messageValue, "wrap");
        container.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                identifier,
                List.of(messageValue, identifier),
                () -> ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired() && identifier.getItemCount() != 0
        ), "wrap");
        container.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                new JLabel("Not Applicable"),
                List.of(messageValue, identifier),
                () -> ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired() && identifier.getItemCount() == 0
        ), "wrap");
        container.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                matchType,
                List.of(messageValue, identifier),
                () -> !((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired() || identifier.getItemCount() != 0
        ), "wrap");
        container.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                text,
                List.of(matchType, messageValue, identifier),
                () -> (!((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired() || identifier.getItemCount() != 0) && ((WizardMatchType) matchType.getSelectedItem()).isMatcher()
        ), "wrap");

        add(new JSeparator(), BorderLayout.PAGE_START);
        add(container, BorderLayout.CENTER);
        if (deletable) {
            JButton delete = new JButton("Delete");
            delete.addActionListener(this::onDelete);
            add(getRightJustifiedButton(delete), BorderLayout.PAGE_END);
        }
    }

    private void resetIdentifiers() {
        identifier.removeAllItems();
        model.getIdentifiers().getOptions().forEach(option -> identifier.addItem(option));
        identifier.setSelectedItem(model.getIdentifiers().getSelectedOption());
    }

    private void resetText() {
        text.setText(model.getText());
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue) messageValue.getSelectedItem());
        resetIdentifiers();
        resetText();
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier((String) identifier.getSelectedItem());
        resetText();
    }

    private void onMatchTypeChanged(ActionEvent actionEvent) {
        model.setMatchType((WizardMatchType) matchType.getSelectedItem());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onDelete(ActionEvent actionEvent) {
        model.setDeleted(true);
    }

    protected Component getRightJustifiedButton(JButton button) {
        JPanel outerContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        outerContainer.setAlignmentX(LEFT_ALIGNMENT);
        outerContainer.setAlignmentY(TOP_ALIGNMENT);
        outerContainer.add(button);
        return outerContainer;
    }
}
