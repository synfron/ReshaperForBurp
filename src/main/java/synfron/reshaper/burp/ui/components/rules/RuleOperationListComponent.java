package synfron.reshaper.burp.ui.components.rules;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.utils.WrapLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.List;

public abstract class RuleOperationListComponent<T extends RuleOperationModel<?,?>> extends JPanel implements IFormComponent {
    protected final ProtocolType protocolType;
    protected final RuleModel model;
    protected JList<T> operationsList;
    protected DefaultListModel<T> operationsListModel;
    protected JComboBox<RuleOperationModelType<?,?>> operationSelector;
    @Getter
    private RuleOperationContainerComponent ruleOperationContainer;
    private final IEventListener<PropertyChangedArgs> ruleOperationChangedListener = this::onRuleOperationChanged;

    public RuleOperationListComponent(ProtocolType protocolType, RuleModel model) {
        this.protocolType = protocolType;
        this.model = model;
        setModelChangedListeners();
        initComponent();
    }

    protected abstract List<T> getRuleOperations();

    private void setModelChangedListeners() {
        getRuleOperations().forEach(model ->
                model.getPropertyChangedEvent().add(ruleOperationChangedListener)
        );
    }

    @SuppressWarnings("unchecked")
    private void onRuleOperationChanged(PropertyChangedArgs propertyChangedArgs) {
        T ruleOperation = (T)propertyChangedArgs.getSource();
        if ("validated".equals(propertyChangedArgs.getName())) {
            int index = operationsListModel.indexOf(ruleOperation);
            operationsListModel.set(index, ruleOperation);
            this.model.markChanged();
        }
    }

    protected void initComponent() {
        setLayout(new BorderLayout());

        add(getOperationsList(), BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private void onSelectionChanged(ListSelectionEvent listSelectionEvent) {
        T model = operationsList.getSelectedValue();
        if (model != null) {
            ruleOperationContainer.setModel(model);
        }
        else if (!defaultSelect()) {
            ruleOperationContainer.setModel(null);
        }
    }

    private boolean defaultSelect() {
        if (operationsList.getSelectedValue() == null && operationsListModel.size() > 0) {
            operationsList.setSelectedIndex(operationsListModel.size() - 1);
            return true;
        }
        return false;
    }

    private Component getOperationsList() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        operationsListModel = new DefaultListModel<T>();
        operationsListModel.addAll(getRuleOperations());

        operationsList = new JList<>(operationsListModel);
        operationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(operationsList);

        operationsList.addListSelectionListener(this::onSelectionChanged);

        container.add(scrollPane);
        return container;
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new WrapLayout(FlowLayout.RIGHT));

        JButton moveUp = new JButton("Move Up");
        JButton moveDown = new JButton("Move Down");
        JButton duplicate = new JButton("Duplicate");
        JButton delete = new JButton("Delete");

        moveUp.addActionListener(this::onMoveUp);
        moveDown.addActionListener(this::onMoveDown);
        duplicate.addActionListener(this::onDuplicate);
        delete.addActionListener(this::onDelete);

        actionBar.add(getAddOperation());
        actionBar.add(moveUp);
        actionBar.add(moveDown);
        actionBar.add(duplicate);
        actionBar.add(delete);

        return actionBar;
    }

    private Component getAddOperation() {
        JPanel container = new JPanel();

        operationSelector = createComboBox(getRuleOperationModelTypes().stream()
                .sorted(Comparator.comparing(RuleOperationModelType::getName))
                .toArray(RuleOperationModelType[]::new));
        JButton add = new JButton("Add");

        add.addActionListener(this::onAdd);

        container.add(operationSelector);
        container.add(add);
        return container;
    }

    private void onDelete(ActionEvent actionEvent) {
        RuleOperationModel<?,?> model = operationsList.getSelectedValue();
        if (model != null) {
            operationsListModel.removeElement(model);
            getRuleOperations().remove(model);
            this.model.markChanged();
        }
    }

    private void onAdd(ActionEvent actionEvent) {
        RuleOperationModelType<?,?> ruleOperationModelType = (RuleOperationModelType<?,?>)operationSelector.getSelectedItem();
        if (ruleOperationModelType != null) {
            T model = getNewModel(ruleOperationModelType);
            model.getPropertyChangedEvent().add(ruleOperationChangedListener);
            operationsListModel.addElement(model);
            getRuleOperations().add(model);
            operationsList.setSelectedValue(model, true);
            this.model.markChanged();
        }
    }

    private void onDuplicate(ActionEvent actionEvent) {
        RuleOperationModel<?,?> templateModel = operationsList.getSelectedValue();
        if (templateModel != null) {
            T model = getModel(templateModel.getRuleOperation().copy());
            model.getPropertyChangedEvent().add(ruleOperationChangedListener);
            operationsListModel.addElement(model);
            getRuleOperations().add(model);
            operationsList.setSelectedValue(model, true);
            this.model.markChanged();
        }
    }

    private void onMoveDown(ActionEvent actionEvent) {
        T ruleOperationModel = operationsList.getSelectedValue();
        int index = operationsList.getSelectedIndex();
        if (ruleOperationModel != null && index < operationsListModel.size() - 1) {
            operationsListModel.remove(index);
            getRuleOperations().remove(index);
            operationsListModel.add(++index, ruleOperationModel);
            getRuleOperations().add(index, ruleOperationModel);
            operationsList.setSelectedIndex(index);
            this.model.markChanged();
        }
    }

    private void onMoveUp(ActionEvent actionEvent) {
        T ruleOperationModel = operationsList.getSelectedValue();
        int index = operationsList.getSelectedIndex();
        if (ruleOperationModel != null && index > 0) {
            operationsListModel.remove(index);
            getRuleOperations().remove(index);
            operationsListModel.add(--index, ruleOperationModel);
            getRuleOperations().add(index, ruleOperationModel);
            operationsList.setSelectedIndex(index);
            this.model.markChanged();
        }
    }

    public void setSelectionContainer(RuleOperationContainerComponent ruleOperationContainer) {
        this.ruleOperationContainer = ruleOperationContainer;
        defaultSelect();
    }

    protected abstract List<RuleOperationModelType<?,?>> getRuleOperationModelTypes();

    protected abstract T getNewModel(RuleOperationModelType<?,?> ruleOperationModelType);

    protected abstract <R extends IRuleOperation<?>> T getModel(R then);
}
