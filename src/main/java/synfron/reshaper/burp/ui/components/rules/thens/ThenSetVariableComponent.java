package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenSetVariable;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetVariableModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSetVariableComponent extends ThenSetComponent<ThenSetVariableModel, ThenSetVariable> {

    private JComboBox<VariableSource> targetSource;
    private JTextField variableName;

    public ThenSetVariableComponent(ThenSetVariableModel then) {
        super(then);
    }

    @Override
    protected List<Component> getExtendedComponents() {
        targetSource = new JComboBox<>(VariableSource.values());
        variableName = new JTextField();

        targetSource.setSelectedItem(model.getTargetSource());
        variableName.setText(model.getVariableName());

        targetSource.addActionListener(this::onTargetSourceChanged);
        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));

        return List.of(
                getLabeledField("Destination Variable Source", targetSource),
                getLabeledField("Destination Variable Name", variableName)
        );
    }

    private void onTargetSourceChanged(ActionEvent actionEvent) {
        model.setTargetSource((VariableSource) targetSource.getSelectedItem());
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName(variableName.getText());
    }
}
