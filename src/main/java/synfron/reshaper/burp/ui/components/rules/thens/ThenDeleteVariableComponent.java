package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenDeleteVariable;
import synfron.reshaper.burp.core.vars.DeleteListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenDeleteVariableModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenDeleteVariableComponent extends ThenComponent<ThenDeleteVariableModel, ThenDeleteVariable> {
    private JComboBox<VariableSource> targetSource;
    private JTextField variableName;
    private JComboBox<DeleteListItemPlacement> itemPlacement;
    private JTextField index;

    public ThenDeleteVariableComponent(ProtocolType protocolType, ThenDeleteVariableModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        targetSource = createComboBox(VariableSource.getAllSettables(protocolType));
        variableName = createTextField(true);
        itemPlacement = createComboBox(DeleteListItemPlacement.values());
        index = createTextField(true);

        targetSource.setSelectedItem(model.getTargetSource());
        variableName.setText(model.getVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        index.setText(model.getIndex());

        targetSource.addActionListener(this::onTargetSourceChanged);
        variableName.getDocument().addDocumentListener(new DocumentActionListener(this::onVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("Variable Source", targetSource), "wrap");
        mainContainer.add(getLabeledField("Variable Name *", variableName), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                targetSource,
                () -> ((VariableSource) targetSource.getSelectedItem()).isList()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                List.of(targetSource, itemPlacement),
                () -> ((VariableSource) targetSource.getSelectedItem()).isList() && ((DeleteListItemPlacement) itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onTargetSourceChanged(ActionEvent actionEvent) {
        model.setTargetSource((VariableSource) targetSource.getSelectedItem());
    }

    private void onVariableNameChanged(ActionEvent actionEvent) {
        model.setVariableName(variableName.getText());
    }

    private void onItemPlacementChanged(ActionEvent actionEvent) {
        model.setItemPlacement((DeleteListItemPlacement) itemPlacement.getSelectedItem());
    }

    private void onIndexChanged(ActionEvent actionEvent) {
        model.setIndex(index.getText());
    }
}
