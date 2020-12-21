package synfron.reshaper.burp.ui.components.rules;

import burp.BurpExtender;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.utils.WrapLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleListComponent extends JPanel {
    private JList<RuleModel> rulesList;
    private DefaultListModel<RuleModel> ruleListModel;
    private RuleContainerComponent ruleContainer;
    private final IEventListener<PropertyChangedArgs> ruleModelChangeListener = this::onRuleModelChange;
    private final IEventListener<CollectionChangedArgs> rulesCollectionChangedListener = this::onRulesCollectionChanged;

    public RuleListComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());

        ruleListModel = new DefaultListModel<>();
        ruleListModel.addAll(BurpExtender.getConnector().getRulesEngine().getRulesRegistry().getRules().stream()
                .map(rule -> new RuleModel(rule).withListener(ruleModelChangeListener))
                .collect(Collectors.toList()));

        rulesList = new JList<>(ruleListModel);
        rulesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(rulesList);

        BurpExtender.getConnector().getRulesEngine().getRulesRegistry().getCollectionChangedEvent().add(rulesCollectionChangedListener);
        rulesList.addListSelectionListener(this::onSelectionChanged);

        add(scrollPane, BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private void onSelectionChanged(ListSelectionEvent listSelectionEvent) {
        RuleModel rule = rulesList.getSelectedValue();
        if (rule != null) {
            ruleContainer.setModel(rule);
        }
        else if (!defaultSelect()) {
            ruleContainer.setModel(null);
        }
    }

    private boolean defaultSelect() {
        if (rulesList.getSelectedValue() == null && ruleListModel.size() > 0) {
            rulesList.setSelectedIndex(ruleListModel.size() - 1);
            return true;
        }
        return false;
    }

    private void onRulesCollectionChanged(CollectionChangedArgs collectionChangedArgs) {
        switch (collectionChangedArgs.getAction()) {
            case Add: {
                Rule item = (Rule)collectionChangedArgs.getItem();
                RuleModel model = new RuleModel(item, true).withListener(ruleModelChangeListener);
                ruleListModel.addElement(model);
                rulesList.setSelectedValue(model, true);
                break;
            }
            case Remove: {
                int index = (int)collectionChangedArgs.getKey();
                ruleListModel.remove(index);
                defaultSelect();
                break;
            }
            case Update: {
                int index = (int)collectionChangedArgs.getKey();
                RuleModel model = ruleListModel.get(index);
                ruleListModel.set(index, model);
                break;
            }
            case Move: {
                int index = (int)collectionChangedArgs.getKey();
                int newIndex = (int) collectionChangedArgs.getNewKey();
                RuleModel model = ruleListModel.remove(index);
                ruleListModel.add(newIndex, model);
                rulesList.setSelectedIndex(newIndex);
                break;
            }
            case Reset: {
                Map<Rule, RuleModel> ruleModelMap = Collections.list(
                        ruleListModel.elements()).stream().collect(Collectors.toMap(RuleModel::getRule, Function.identity())
                );
                ruleListModel.clear();
                ruleListModel.addAll(BurpExtender.getConnector().getRulesEngine().getRulesRegistry().getRules().stream()
                        .map(rule -> ruleModelMap.containsKey(rule) ?
                                ruleModelMap.get(rule) :
                                new RuleModel(rule).withListener(ruleModelChangeListener)
                        ).collect(Collectors.toList()));
                defaultSelect();
                break;
            }
        }
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new WrapLayout());

        JButton add = new JButton("Add");
        JButton moveUp = new JButton("Move Up");
        JButton moveDown = new JButton("Move Down");
        JButton delete = new JButton("Delete");

        add.addActionListener(this::onAdd);
        moveUp.addActionListener(this::onMoveUp);
        moveDown.addActionListener(this::onMoveDown);
        delete.addActionListener(this::onDelete);

        actionBar.add(add);
        actionBar.add(moveUp);
        actionBar.add(moveDown);
        actionBar.add(delete);

        return actionBar;
    }

    private void onRuleModelChange(PropertyChangedArgs propertyChangedArgs) {
        RuleModel model = (RuleModel)propertyChangedArgs.getSource();
        int index = ruleListModel.indexOf(model);
        ruleListModel.set(index, model);
    }

    private void onDelete(ActionEvent actionEvent) {
        RuleModel rule = rulesList.getSelectedValue();
        if (rule != null) {
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().deleteRule(rule.getRule());
        }
    }

    private void onMoveDown(ActionEvent actionEvent) {
        RuleModel rule = rulesList.getSelectedValue();
        int index = rulesList.getSelectedIndex();
        if (rule != null && index < ruleListModel.size() - 1) {
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().moveNext(rule.getRule());
        }
    }

    private void onMoveUp(ActionEvent actionEvent) {
        RuleModel rule = rulesList.getSelectedValue();
        int index = rulesList.getSelectedIndex();
        if (rule != null && index > 0) {
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().movePrevious(rule.getRule());
        }
    }

    private void onAdd(ActionEvent actionEvent) {
        Rule rule = new Rule();
        rule.setEnabled(false);

        BurpExtender.getConnector().getRulesEngine().getRulesRegistry().addRule(rule);
    }

    public void setSelectionContainer(RuleContainerComponent ruleContainer) {
        this.ruleContainer = ruleContainer;

        if (ruleListModel.size() == 0) {
            Rule rule = new Rule();
            rule.setEnabled(false);
            BurpExtender.getConnector().getRulesEngine().getRulesRegistry().addRule(rule);
        }
        defaultSelect();
    }
}
