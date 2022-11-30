package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.ui.models.rules.whens.WhenMatchesTextModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class WhenMatchesTextComponent extends WhenComponent<WhenMatchesTextModel, WhenMatchesText> {
    protected JCheckBox useMessageValue;
    protected JComboBox<MessageValue> messageValue;
    protected JTextField identifier;
    protected JComboBox<GetItemPlacement> identifierPlacement;
    protected JComboBox<MessageValueType> messageValueType;
    private JTextField messageValuePath;
    protected JTextField sourceText;
    protected JTextField matchText;
    protected JComboBox<MatchType> matchType;

    public WhenMatchesTextComponent(ProtocolType protocolType, WhenMatchesTextModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        useMessageValue = new JCheckBox("Use Message Value");
        messageValue = createComboBox(Arrays.stream(MessageValue.values())
                .filter(value -> value.isGettable(protocolType))
                .toArray(MessageValue[]::new));
        identifier = createTextField(true);
        identifierPlacement = createComboBox(GetItemPlacement.values());
        sourceText = createTextField(true);
        messageValueType = createComboBox(MessageValueType.values());
        messageValuePath = createTextField(true);
        matchType = createComboBox(MatchType.values());
        matchText = createTextField(true);

        useMessageValue.setSelected(model.isUseMessageValue());
        messageValue.setSelectedItem(model.getMessageValue());
        identifier.setText(model.getIdentifier());
        identifierPlacement.setSelectedItem(model.getIdentifierPlacement());
        sourceText.setText(model.getSourceText());
        messageValueType.setSelectedItem(model.getMessageValueType());
        messageValuePath.setText(model.getMessageValuePath());
        matchType.setSelectedItem(model.getMatchType());
        matchText.setText(model.getMatchText());

        useMessageValue.addActionListener(this::onUseMessageValueChanged);
        messageValue.addActionListener(this::onMessageValueChanged);
        identifier.getDocument().addDocumentListener(new DocumentActionListener(this::onIdentifierChanged));
        identifierPlacement.addActionListener(this::onIdentifierPlacementChanged);
        sourceText.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceTextChanged));
        messageValueType.addActionListener(this::onMessageValueTypeChanged);
        messageValuePath.getDocument().addDocumentListener(new DocumentActionListener(this::onMessageValuePathChanged));
        matchType.addActionListener(this::onMatchTypeChanged);
        matchText.getDocument().addDocumentListener(new DocumentActionListener(this::onMatchTextChanged));

        mainContainer.add(useMessageValue, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Message Value", messageValue),
                useMessageValue,
                () -> useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Identifier *", identifier),
                List.of(useMessageValue, messageValue),
                () -> useMessageValue.isSelected() && ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Identifier Placement", identifierPlacement),
                List.of(useMessageValue, messageValue),
                () -> useMessageValue.isSelected() && ((MessageValue) messageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Text", sourceText),
                useMessageValue,
                () -> !useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(getLabeledField("Source Value Type", messageValueType), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Value Path", messageValuePath),
                List.of(useMessageValue, messageValueType),
                () -> messageValueType.getSelectedItem() != MessageValueType.Text
        ), "wrap");
        mainContainer.add(getLabeledField("Match Type", matchType), "wrap");
        mainContainer.add(getLabeledField("Match Text", matchText), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
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

    private void onIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setIdentifierPlacement((GetItemPlacement) identifierPlacement.getSelectedItem());
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
