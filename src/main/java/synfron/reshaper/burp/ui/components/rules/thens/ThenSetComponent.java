package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.thens.ThenSet;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public abstract class ThenSetComponent<P extends ThenSetModel<P, T>, T extends ThenSet<T>> extends ThenComponent<P, T> {
    protected JCheckBox useMessageValue;
    protected JComboBox<MessageValue> sourceMessageValue;
    protected JTextField sourceIdentifier;
    protected JComboBox<GetItemPlacement> sourceIdentifierPlacement;
    protected JComboBox<MessageValueType> sourceMessageValueType;
    private JTextField sourceMessageValuePath;
    protected JCheckBox useReplace;
    protected JTextField regexPattern;
    protected JTextField text;
    protected JTextField replacementText;
    protected JComboBox<MessageValueType> destinationMessageValueType;
    protected JTextField destinationMessageValuePath;

    public ThenSetComponent(ProtocolType protocolType, P then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        useMessageValue = new JCheckBox("Use Message Value");
        sourceMessageValue = createComboBox(Arrays.stream(MessageValue.values())
                .filter(value -> value.isGettable(protocolType)).toArray(MessageValue[]::new));
        sourceIdentifier = createTextField(true);
        sourceIdentifierPlacement = createComboBox(GetItemPlacement.values());
        sourceMessageValueType = createComboBox(MessageValueType.values());
        sourceMessageValuePath = createTextField(true);
        useReplace = new JCheckBox("Use Regex Replace");
        regexPattern = createTextField(true);
        text = createTextField(true);
        replacementText = createTextField(true);
        destinationMessageValueType = createComboBox(MessageValueType.values());
        destinationMessageValuePath = createTextField(true);

        useMessageValue.setSelected(model.isUseMessageValue());
        sourceMessageValue.setSelectedItem(model.getSourceMessageValue());
        sourceIdentifier.setText(model.getSourceIdentifier());
        sourceIdentifierPlacement.setSelectedItem(model.getSourceIdentifierPlacement());
        sourceMessageValueType.setSelectedItem(model.getSourceMessageValueType());
        sourceMessageValuePath.setText(model.getSourceMessageValuePath());
        useReplace.setSelected(model.isUseReplace());
        regexPattern.setText(model.getRegexPattern());
        text.setText(model.getText());
        replacementText.setText(model.getReplacementText());
        destinationMessageValueType.setSelectedItem(model.getDestinationMessageValueType());
        destinationMessageValuePath.setText(model.getDestinationMessageValuePath());

        useMessageValue.addActionListener(this::onUseMessageValueChanged);
        sourceMessageValue.addActionListener(this::onSourceMessageValueChanged);
        sourceIdentifier.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceIdentifierChanged));
        sourceIdentifierPlacement.addActionListener(this::onSourceIdentifierPlacementChanged);
        sourceMessageValueType.addActionListener(this::onSourceMessageValueTypeChanged);
        sourceMessageValuePath.getDocument().addDocumentListener(new DocumentActionListener(this::onSourceMessageValuePathChanged));
        useReplace.addActionListener(this::onUseReplaceChanged);
        regexPattern.getDocument().addDocumentListener(new DocumentActionListener(this::onRegexPatternChanged));
        text.getDocument().addDocumentListener(new DocumentActionListener(this::onTextChanged));
        replacementText.getDocument().addDocumentListener(new DocumentActionListener(this::onReplacementTextChanged));
        destinationMessageValueType.addActionListener(this::onDestinationMessageValueTypeChanged);
        destinationMessageValuePath.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationMessageValuePathChanged));

        mainContainer.add(useMessageValue, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Message Value", sourceMessageValue),
                useMessageValue,
                () -> useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Identifier *", sourceIdentifier),
                List.of(useMessageValue, sourceMessageValue),
                () -> useMessageValue.isSelected() && ((MessageValue)sourceMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Identifier Placement", sourceIdentifierPlacement),
                List.of(useMessageValue, sourceMessageValue),
                () -> useMessageValue.isSelected() && ((MessageValue)sourceMessageValue.getSelectedItem()).isIdentifierRequired()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Text", text),
                useMessageValue,
                () -> !useMessageValue.isSelected()
        ), "wrap");
        mainContainer.add(getLabeledField("Source Value Type", sourceMessageValueType), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Source Value Path", sourceMessageValuePath),
                List.of(sourceMessageValueType),
                () -> sourceMessageValueType.getSelectedItem() != MessageValueType.Text
        ), "wrap");
        mainContainer.add(useReplace, "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Regex Pattern", regexPattern),
                useReplace,
                () -> useReplace.isSelected()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Regex Replacement Text", replacementText),
                useReplace,
                () -> useReplace.isSelected()
        ), "wrap");
        getExtendedComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getLabeledField("Destination Value Type", destinationMessageValueType), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Destination Value Path", destinationMessageValuePath),
                destinationMessageValueType,
                () -> destinationMessageValueType.getSelectedItem() != MessageValueType.Text
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onUseMessageValueChanged(ActionEvent actionEvent) {
        model.setUseMessageValue(useMessageValue.isSelected());
    }

    private void onSourceMessageValueChanged(ActionEvent actionEvent) {
        model.setSourceMessageValue((MessageValue) sourceMessageValue.getSelectedItem());
    }

    private void onSourceIdentifierChanged(ActionEvent actionEvent) {
        model.setSourceIdentifier(sourceIdentifier.getText());
    }

    private void onSourceIdentifierPlacementChanged(ActionEvent actionEvent) {
        model.setSourceIdentifierPlacement((GetItemPlacement) sourceIdentifierPlacement.getSelectedItem());
    }

    private void onSourceMessageValueTypeChanged(ActionEvent actionEvent) {
        model.setSourceMessageValueType((MessageValueType) sourceMessageValueType.getSelectedItem());
    }

    private void onSourceMessageValuePathChanged(ActionEvent actionEvent) {
        model.setSourceMessageValuePath(sourceMessageValuePath.getText());
    }

    private void onUseReplaceChanged(ActionEvent actionEvent) {
        model.setUseReplace(useReplace.isSelected());
    }

    private void onRegexPatternChanged(ActionEvent actionEvent) {
        model.setRegexPattern(regexPattern.getText());
    }

    private void onTextChanged(ActionEvent actionEvent) {
        model.setText(text.getText());
    }

    private void onReplacementTextChanged(ActionEvent actionEvent) {
        model.setReplacementText(replacementText.getText());
    }

    private void onDestinationMessageValueTypeChanged(ActionEvent actionEvent) {
        model.setDestinationMessageValueType((MessageValueType) destinationMessageValueType.getSelectedItem());
    }

    private void onDestinationMessageValuePathChanged(ActionEvent actionEvent) {
        model.setDestinationMessageValuePath(destinationMessageValuePath.getText());
    }

    protected abstract List<Component> getExtendedComponents();
}
