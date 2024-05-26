package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRepeat;
import synfron.reshaper.burp.core.rules.thens.entities.repeat.RepeatCondition;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRepeatModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class ThenRepeatComponent extends ThenComponent<ThenRepeatModel, ThenRepeat> {

    private JTextField subGroupCount;
    private JComboBox<RepeatCondition> repeatCondition;
    private JComboBox<VariableSource> listVariableSource;
    private JTextField listVariableName;
    private JTextField entryVariableName;
    private JTextField count;
    private JTextField booleanValue;
    protected JTextField maxCount;

    public ThenRepeatComponent(ProtocolType protocolType, ThenRepeatModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        subGroupCount = createTextField(false);
        repeatCondition = createComboBox(RepeatCondition.values());
        count = createTextField(true);
        listVariableSource = createComboBox(Arrays.stream(VariableSource.values())
                .filter(VariableSource::isList)
                .toArray(VariableSource[]::new));
        listVariableName = createTextField(true);
        entryVariableName = createTextField(true);
        JTextArea booleanValueWarning = new JTextArea("Warning: Boolean Value should contain a variable tag whose value would change between the repeat iterations in order to avoid unexpected repeating.");
        booleanValue = createTextField(true);
        maxCount = createTextField(false);

        subGroupCount.setText(model.getSubGroupCount());
        repeatCondition.setSelectedItem(model.getRepeatCondition());
        count.setText(model.getCount());
        listVariableSource.setSelectedItem(model.getListVariableSource());
        listVariableName.setText(model.getListVariableName());
        entryVariableName.setText(model.getEntryVariableName());
        booleanValue.setText(model.getBooleanValue());
        maxCount.setText(model.getMaxCount());

        booleanValueWarning.setLineWrap(true);
        booleanValueWarning.setSize(subGroupCount.getPreferredSize().width, booleanValueWarning.getPreferredSize().height);
        booleanValueWarning.setWrapStyleWord(true);
        booleanValueWarning.setOpaque(false);

        subGroupCount.getDocument().addDocumentListener(new DocumentActionListener(this::onSubGroupCountChanged));
        repeatCondition.addActionListener(this::onRepeatConditionChanged);
        count.getDocument().addDocumentListener(new DocumentActionListener(this::onCountChanged));
        listVariableSource.addActionListener(this::onListVariableSourceChanged);
        listVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onListVariableNameChanged));
        entryVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onEntryVariableNameChanged));
        booleanValue.getDocument().addDocumentListener(new DocumentActionListener(this::onBooleanValueChanged));
        maxCount.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxCountChanged));

        mainContainer.add(getLabeledField("Number of Following Thens Included *", subGroupCount), "wrap");
        mainContainer.add(getLabeledField("Repeat Condition", repeatCondition), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Count *", count),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.Count
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("List Variable Source *", listVariableSource),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.HasNextItem
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("List Variable Name *", listVariableName),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.HasNextItem
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Event Variable Name *", entryVariableName),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.HasNextItem
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                booleanValueWarning,
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.WhileTrue
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Boolean Value *", booleanValue),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.WhileTrue
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Max Count *", maxCount),
                repeatCondition,
                () -> repeatCondition.getSelectedItem() == RepeatCondition.WhileTrue
        ), "wrap");
    }

    private void onListVariableSourceChanged(ActionEvent actionEvent) {
        model.setListVariableSource((VariableSource) listVariableSource.getSelectedItem());
    }

    private void onListVariableNameChanged(ActionEvent actionEvent) {
        model.setListVariableName(listVariableName.getText());
    }

    private void onSubGroupCountChanged(ActionEvent actionEvent) {
        model.setSubGroupCount(subGroupCount.getText());
    }

    private void onRepeatConditionChanged(ActionEvent actionEvent) {
        model.setRepeatCondition((RepeatCondition) repeatCondition.getSelectedItem());
    }

    private void onEntryVariableNameChanged(ActionEvent actionEvent) {
        model.setEntryVariableName(entryVariableName.getText());
    }

    private void onBooleanValueChanged(ActionEvent actionEvent) {
        model.setBooleanValue(booleanValue.getText());
    }

    private void onCountChanged(ActionEvent actionEvent) {
        model.setCount(count.getText());
    }

    private void onMaxCountChanged(ActionEvent actionEvent) {
        model.setMaxCount(maxCount.getText());
    }
}
