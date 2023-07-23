package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenEvaluate;
import synfron.reshaper.burp.core.rules.thens.entities.evaluate.Operation;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.thens.ThenEvaluateModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ThenEvaluateComponent extends ThenComponent<ThenEvaluateModel, ThenEvaluate> {
    private JTextField x;
    private JComboBox<Operation> operation;
    private JTextField y;
    private JComboBox<VariableSource> destinationVariableSource;
    private JTextField destinationVariableName;
    private JComboBox<SetListItemPlacement> itemPlacement;
    private JTextField delimiter;
    private JTextField index;

    public ThenEvaluateComponent(ProtocolType protocolType, ThenEvaluateModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        x = createTextField(true);
        operation = createComboBox(Operation.values());
        y = createTextField(true);
        destinationVariableSource = createComboBox(VariableSource.getAllSettables(protocolType));
        destinationVariableName = createTextField(true);
        itemPlacement = createComboBox(SetListItemPlacement.values());
        delimiter = createTextField(true);
        index = createTextField(true);

        x.setText(model.getX());
        operation.setSelectedItem(model.getOperation());
        y.setText(model.getY());
        destinationVariableSource.setSelectedItem(model.getDestinationVariableSource());
        destinationVariableName.setText(model.getDestinationVariableName());
        itemPlacement.setSelectedItem(model.getItemPlacement());
        delimiter.setText(model.getDelimiter());
        index.setText(model.getIndex());

        x.getDocument().addDocumentListener(new DocumentActionListener(this::onXChanged));
        operation.addActionListener(this::onOperationChanged);
        y.getDocument().addDocumentListener(new DocumentActionListener(this::onYChanged));
        destinationVariableSource.addActionListener(this::onDestinationVariableSourceChanged);
        destinationVariableName.getDocument().addDocumentListener(new DocumentActionListener(this::onDestinationVariableNameChanged));
        itemPlacement.addActionListener(this::onItemPlacementChanged);
        delimiter.getDocument().addDocumentListener(new DocumentActionListener(this::onDelimiterChanged));
        index.getDocument().addDocumentListener(new DocumentActionListener(this::onIndexChanged));

        mainContainer.add(getLabeledField("X *", x), "wrap");
        mainContainer.add(getLabeledField("Operation", operation), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Y *", y),
                operation,
                () -> ((Operation)operation.getSelectedItem()).getInputs() == 2
        ), "wrap");
        mainContainer.add(getLabeledField("Destination Variable Source", destinationVariableSource), "wrap");
        mainContainer.add(getLabeledField("Destination Variable Name *", destinationVariableName), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Item Placement", itemPlacement),
                destinationVariableSource,
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Delimiter *", delimiter),
                List.of(destinationVariableSource, itemPlacement),
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasDelimiterSetter()
        ), "wrap");
        mainContainer.add(ComponentVisibilityManager.withVisibilityFieldChangeDependency(
                getLabeledField("Index *", index),
                List.of(destinationVariableSource, itemPlacement),
                () -> ((VariableSource)destinationVariableSource.getSelectedItem()).isList() && ((SetListItemPlacement)itemPlacement.getSelectedItem()).isHasIndexSetter()
        ), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onXChanged(ActionEvent actionEvent) {
        model.setX(x.getText());
    }

    private void onOperationChanged(ActionEvent actionEvent) {
        model.setOperation((Operation) operation.getSelectedItem());
    }

    private void onYChanged(ActionEvent actionEvent) {
        model.setY(y.getText());
    }

    private void onDestinationVariableSourceChanged(ActionEvent actionEvent) {
        model.setDestinationVariableSource((VariableSource) destinationVariableSource.getSelectedItem());
    }

    private void onDestinationVariableNameChanged(ActionEvent actionEvent) {
        model.setDestinationVariableName(destinationVariableName.getText());
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
