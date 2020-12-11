package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.ui.models.rules.whens.WhenMatchesTextModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class WhenMatchesTextComponent extends WhenComponent<WhenMatchesTextModel, WhenMatchesText> {
    protected JCheckBox useMessageValue;
    protected JComboBox<MessageValue> messageValue;
    protected JTextField identifier;
    protected JComboBox<MessageValueType> messageValueType;
    private JTextField messageValuePath;
    protected JTextField sourceText;
    protected JTextField matchText;
    protected JComboBox<MatchType> matchType;

    public WhenMatchesTextComponent(WhenMatchesTextModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        useMessageValue = new JCheckBox("Use Message Value");
        messageValue = new JComboBox<>(MessageValue.values());
        identifier = new JTextField();
        messageValueType = new JComboBox<>(MessageValueType.values());
        messageValuePath = new JTextField();
        sourceText = new JTextField();
        matchText = new JTextField();
        matchType = new JComboBox<>(MatchType.values());
        JButton save = new JButton("Save");

        useMessageValue.setSelected(model.isUseMessageValue());
        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());
        messageValueType.setSelectedItem(model.getMessageValueType());
        messageValuePath.setText(model.getMessageValuePath());
        sourceText.setText(model.getSourceText());
        matchText.setText(model.getMatchText());
        matchType.setSelectedItem(model.getMatchType());

        useMessageValue.addActionListener(this::onUseMessageValueChanged);
        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        messageValueType.addActionListener(this::onMessageValueTypeChanged);
        messageValuePath.getDocument().addDocumentListener(new DocumentActionListener(this::onMessageValuePathChanged));
        sourceText.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceTextChanged));
        matchText.getDocument().addDocumentListener(new DocumentActionListener(this::onMatchTextChanged));
        matchType.addActionListener(this::onMatchTypeChanged);
        save.addActionListener(this::onSave);

        mainContainer.add(useMessageValue, "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Message Value", messageValue),
                useMessageValue,
                () -> useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Identifier", identifier),
                List.of(useMessageValue, messageValue),
                () -> useMessageValue.isSelected() && MessageValueHandler.hasIdentifier((MessageValue) messageValue.getSelectedItem())
        ), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Source Text", sourceText),
                useMessageValue,
                () -> !useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(getLabeledField("Message Value Type", messageValueType), "wrap");
        mainContainer.add(withVisibilityFieldChangeDependency(
                getLabeledField("Message Value Path", messageValuePath),
                List.of(useMessageValue, messageValueType),
                () -> messageValueType.getSelectedItem() != MessageValueType.Text
        ), "wrap");
        mainContainer.add(getLabeledField("Match Text", matchText), "wrap");
        mainContainer.add(getLabeledField("Match Type", matchType), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(save));
    }

    private void onMatchTypeChanged(ActionEvent actionEvent) {
        model.setMatchType((MatchType) matchType.getSelectedItem());
    }

    private void onUseMessageValueChanged(ActionEvent actionEvent) {
        model.setUseMessageValue(useMessageValue.isSelected());
    }

    private void onMessageValueChanged(ActionEvent actionEvent) {
        model.setMessageValue((MessageValue) messageValue.getSelectedItem());
    }

    private void onIdentifierChanged(ActionEvent actionEvent) {
        model.setIdentifier(identifier.getText());
    }

    private void onMessageValueTypeChanged(ActionEvent actionEvent) {
        model.setMessageValueType((MessageValueType) messageValueType.getSelectedItem());
    }

    private void onMessageValuePathChanged(ActionEvent actionEvent) {
        model.setMessageValuePath(messageValuePath.getText());
    }

    private void onSourceTextChanged(ActionEvent actionEvent) {
        model.setSourceText(sourceText.getText());
    }

    private void onMatchTextChanged(ActionEvent actionEvent) {
        model.setMatchText(matchText.getText());
    }
}
