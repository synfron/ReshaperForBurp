package synfron.reshaper.burp.ui.components.vars;

import com.alexandriasoftware.swing.JSplitButton;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.Variables;
import synfron.reshaper.burp.ui.models.vars.VariableModel;
import synfron.reshaper.burp.ui.utils.ListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VariableListComponent extends JPanel {
    private JList<VariableModel> variableList;
    private DefaultListModel<VariableModel> variableListModel;
    private VariableContainerComponent variableContainer;
    private final IEventListener<CollectionChangedArgs> variablesCollectionChangedListener = this::onVariablesCollectionChanged;
    private final IEventListener<PropertyChangedArgs> variableModelChangedListener = this::onVariableModelChanged;
    private final IEventListener<PropertyChangedArgs> newVariableModelChangedListener = this::onNewVariableModelChanged;
    private JRadioButtonMenuItem listVariable;

    public VariableListComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        variableListModel = new DefaultListModel<>();
        variableListModel.addAll(GlobalVariables.get().getValues().stream()
                .map(variable -> new VariableModel(variable).withListener(variableModelChangedListener))
                .collect(Collectors.toList()));

        variableList = new JList<>(variableListModel);
        variableList.setCellRenderer(new ListCellRenderer());
        variableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(variableList);

        GlobalVariables.get().getCollectionChangedEvent().add(variablesCollectionChangedListener);
        variableList.addListSelectionListener(this::onSelectionChanged);

        add(scrollPane, BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private void onSelectionChanged(ListSelectionEvent listSelectionEvent) {
        VariableModel variable = variableList.getSelectedValue();
        if (variable != null) {
            variableContainer.setModel(variable);
        }
        else if (!defaultSelect()) {
            variableContainer.setModel(null);
        }
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton delete = new JButton("Delete");

        delete.addActionListener(this::onDelete);

        actionBar.add(getAddButton());
        actionBar.add(delete);

        return actionBar;
    }

    private JSplitButton getAddButton() {
        JSplitButton add = new JSplitButton("Add    ");
        JPopupMenu variableTypeOptions = new JPopupMenu();

        ButtonGroup variableType = new ButtonGroup();
        JRadioButtonMenuItem singleVariable = new JRadioButtonMenuItem("Single");
        listVariable = new JRadioButtonMenuItem("List");

        singleVariable.setSelected(true);
        singleVariable.setActionCommand("Single");
        listVariable.setSelected(false);
        listVariable.setActionCommand("List");

        add.addButtonClickedActionListener(this::onAdd);

        variableType.add(singleVariable);
        variableType.add(listVariable);

        variableTypeOptions.add(singleVariable);
        variableTypeOptions.add(listVariable);

        add.setPopupMenu(variableTypeOptions);

        return add;
    }

    private void onAdd(ActionEvent actionEvent) {
        VariableModel model = new VariableModel(listVariable.isSelected()).withListener(newVariableModelChangedListener);
        variableListModel.addElement(model);
        variableList.setSelectedValue(model, true);
    }

    private void onNewVariableModelChanged(PropertyChangedArgs propertyChangedArgs) {
        VariableModel model = (VariableModel)propertyChangedArgs.getSource();
        switch (propertyChangedArgs.getName()) {
            case "saved" -> {
                if ((boolean) propertyChangedArgs.getValue()) {
                    variableListModel.removeElement(model);
                    defaultSelect();
                }
            }
            case "name" -> {
                int index = variableListModel.indexOf(model);
                variableListModel.set(index, model);
            }
        }
    }

    private void onDelete(ActionEvent actionEvent) {
        VariableModel variable = variableList.getSelectedValue();
        if (variable != null) {
            if (!GlobalVariables.get().remove(Variables.asKey(variable.getName(), variable.isList()))) {
                variableListModel.removeElement(variable);
            }
        }
    }

    private boolean defaultSelect() {
        if (variableList.getSelectedValue() == null && variableListModel.size() > 0) {
            variableList.setSelectedIndex(variableListModel.size() - 1);
            return true;
        }
        return false;
    }

    private void onVariablesCollectionChanged(CollectionChangedArgs collectionChangedArgs) {
        SwingUtilities.invokeLater(() -> {
            Variable item = (Variable) collectionChangedArgs.getItem();
            switch (collectionChangedArgs.getAction()) {
                case Add -> {
                    VariableModel variableModel = new VariableModel(item).withListener(variableModelChangedListener);
                    variableListModel.addElement(variableModel);
                    defaultSelect();
                }
                case Remove -> {
                    variableListModel.removeElement(new VariableModel(item));
                    defaultSelect();
                }
                case Update -> {
                    int index = variableListModel.indexOf(new VariableModel(item));
                    if (index >= 0) {
                        VariableModel model = variableListModel.get(index);
                        variableListModel.set(index, model);
                    }
                }
                case Reset -> {
                    List<VariableModel> currentModels = Collections.list(variableListModel.elements());
                    Map<Variable, VariableModel> variableModelMap = currentModels.stream()
                            .filter(variableModel -> variableModel.getVariable() != null)
                            .collect(Collectors.toMap(VariableModel::getVariable, Function.identity()));
                    Stream<VariableModel> draftModels = currentModels.stream()
                            .filter(model -> model.getVariable() == null);
                    variableListModel.clear();
                    variableListModel.addAll(Stream.concat(
                            GlobalVariables.get().getValues().stream()
                                    .map(variable -> variableModelMap.containsKey(variable) ?
                                            variableModelMap.get(variable) :
                                            new VariableModel(variable).withListener(variableModelChangedListener)
                                    ),
                            draftModels
                    ).collect(Collectors.toList()));
                    defaultSelect();
                }
            }
        });
    }

    private void onVariableModelChanged(PropertyChangedArgs propertyChangedArgs) {
        VariableModel model = (VariableModel)propertyChangedArgs.getSource();
        switch (propertyChangedArgs.getName()) {
            case "saved", "name" -> {
                int index = variableListModel.indexOf(model);
                if (index >= 0) {
                    variableListModel.set(index, model);
                }
            }
        }
    }

    public void setSelectionContainer(VariableContainerComponent variableContainer) {
        this.variableContainer = variableContainer;

        if (variableListModel.size() == 0) {
            VariableModel variableModel = new VariableModel(listVariable.isSelected()).withListener(newVariableModelChangedListener);
            variableListModel.addElement(variableModel);
        }
        defaultSelect();
    }
}
