package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSetVariable;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetVariableModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenSetVariableComponent extends ThenSetComponent<ThenSetVariableModel, ThenSetVariable> {

    private JComboBox<VariableSource> targetSource;
    private JTextField variableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenSetVariableComponent(ProtocolType protocolType, ThenSetVariableModel then) {
        super(protocolType, then);
    }

    @Override
    protected List<Component> getExtendedComponents() {
        targetSource = createComboBox(VariableSource.getAllSettables(protocolType));
        variableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        targetSource.setSelectedItem(model.getTargetSource());
        variableName.setText(model.getVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        targetSource.addActionListener(this::onTargetSourceChanged);
        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        return List.of(
                getLabeledField("Destination Variable Source", targetSource),
                getLabeledField("Destination Variable Name *", variableName),
                ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                        getLabeledField("Item Placement", itemPlacement),
                        targetSource,
                        () -> ((VariableSource)targetSource.getSelectedItem()).isList()
                ),
                ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                        getLabeledField("Delimiter *", delimiter),
                        List.of(targetSource, itemPlacement),
                        () -> ((VariableSource)targetSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
                ),
                ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                        getLabeledField("Index *", index),
                        List.of(targetSource, itemPlacement),
                        () -> ((VariableSource)targetSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
                )
        );
    }

    private void onTargetSourceChanged(ActionEvent actionEvent) {
        model.setTargetSource((VariableSource) targetSource.getSelectedItem());
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName(variableName.getText());
    }

    private void onItemPlacementChanged(ActionEvent actionEvent) {
        model.setItemPlacement((SetListItemPlacement)itemPlacement.getSelectedItem());
    }

    private void onDelimiterChanged(ActionEvent actionEvent) {
        model.setDelimiter(delimiter.getText());
    }

    private void onIndexChanged(ActionEvent actionEvent) {
        model.setIndex(index.getText());
    }
}
