package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenRepeat;
import synfron.reshaper.burp.core.rules.whens.entities.repeat.SuccessCriteria;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.whens.WhenRepeatModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class WhenRepeatComponent extends WhenComponent<WhenRepeatModel, WhenRepeat> {

    protected JTextField subGroupCount;
    protected JComboBox<SuccessCriteria> successCriteria;
    protected JComboBox<VariableSource> listVariableSource;
    protected JTextField listVariableName;
    protected JTextField entryVariableName;

    public WhenRepeatComponent(ProtocolType protocolType, WhenRepeatModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        subGroupCount = createTextField(false);
        successCriteria = createComboBox(SuccessCriteria.values());
        listVariableSource = createComboBox(Arrays.stream(VariableSource.values())
                .filter(VariableSource::isList)
                .toArray(VariableSource[]::new));
        listVariableName = createTextField(true);
        entryVariableName = createTextField(true);

        subGroupCount.setText(model.getSubGroupCount());
        successCriteria.setSelectedItem(model.getSuccessCriteria());
        listVariableSource.setSelectedItem(model.getListVariableSource());
        listVariableName.setText(model.getListVariableName());
        entryVariableName.setText(model.getEntryVariableName());

        subGroupCount.getDocument().addDocumentListener(new DocumentActionListener(this::onSubGroupCountChanged));
        successCriteria.addActionListener(this::onSuccessCriteriaChanged);
        listVariableSource.addActionListener(this::onListVariableSourceChanged);
        listVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onListVariableNameChanged));
        entryVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onEntryVariableNameChanged));

        mainContainer.add(getLabeledField("Number of Following Whens Included *", subGroupCount), "wrap");
        mainContainer.add(getLabeledField("Success Criteria", successCriteria), "wrap");
        mainContainer.add(getLabeledField("List Variable Source *", listVariableSource), "wrap");
        mainContainer.add(getLabeledField("List Variable Name *", listVariableName), "wrap");
        mainContainer.add(getLabeledField("Item Event Variable Name *", entryVariableName), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
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

    private void onSuccessCriteriaChanged(ActionEvent actionEvent) {
        model.setSuccessCriteria((SuccessCriteria) successCriteria.getSelectedItem());
    }

    private void onEntryVariableNameChanged(ActionEvent actionEvent) {
        model.setEntryVariableName(entryVariableName.getText());
    }
}
