package synfron.reshaper.burp.ui.components.vars;

import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.ui.models.vars.VariableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

public class VariableListComponent extends JPanel {
    private JList<VariableModel> variableList;
    private DefaultListModel<VariableModel> variableListModel;
    private VariableContainerComponent variableContainer;
    private final IEventListener<CollectionChangedArgs> variablesCollectionChangedListener = this::onVariablesCollectionChanged;
    private final IEventListener<PropertyChangedArgs> variableModelChangedListener = this::onVariableModelChanged;
    private final IEventListener<PropertyChangedArgs> newVariableModelChangedListener = this::onNewVariableModelChanged;

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

        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");

        delete.addActionListener(this::onDelete);
        add.addActionListener(this::onAdd);

        actionBar.add(add);
        actionBar.add(delete);

        return actionBar;
    }

    private void onAdd(ActionEvent actionEvent) {
        VariableModel model = new VariableModel().withListener(newVariableModelChangedListener);
        variableListModel.addElement(model);
        variableList.setSelectedValue(model, true);
    }

    private void onNewVariableModelChanged(PropertyChangedArgs propertyChangedArgs) {
        VariableModel model = (VariableModel)propertyChangedArgs.getSource();
        switch (propertyChangedArgs.getName()) {
            case "saved": {
                if ((boolean)propertyChangedArgs.getValue()) {
                    variableListModel.removeElement(model);
                    defaultSelect();
                }
                break;
            }
            case "name": {
                int index = variableListModel.indexOf(model);
                variableListModel.set(index, model);
            }
        }
    }

    private void onDelete(ActionEvent actionEvent) {
        VariableModel variable = variableList.getSelectedValue();
        if (variable != null) {
            if (!GlobalVariables.get().remove(variable.getName())) {
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
                case Add:
                    variableListModel.addElement(new VariableModel(item).withListener(variableModelChangedListener));
                    defaultSelect();
                    break;
                case Remove:
                    variableListModel.removeElement(new VariableModel(item));
                    defaultSelect();
                    break;
                case Update: {
                    int index = variableListModel.indexOf(new VariableModel(item));
                    if (index >= 0) {
                        VariableModel model = variableListModel.get(index);
                        variableListModel.set(index, model);
                    }
                    break;
                }
            }
        });
    }

    private void onVariableModelChanged(PropertyChangedArgs propertyChangedArgs) {
        VariableModel model = (VariableModel)propertyChangedArgs.getSource();
        switch (propertyChangedArgs.getName()) {
            case "saved":
            case "name": {
                int index = variableListModel.indexOf(model);
                variableListModel.set(index, model);
            }
        }
    }

    public void setSelectionContainer(VariableContainerComponent variableContainer) {
        this.variableContainer = variableContainer;

        if (variableListModel.size() == 0) {
            variableListModel.addElement(new VariableModel().withListener(newVariableModelChangedListener));
        }
        defaultSelect();
    }
}
