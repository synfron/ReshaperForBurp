package synfron.reshaper.burp.ui.components.rules.wizard.whens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.whens.WhenWizardItemModel;
import synfron.reshaper.burp.ui.models.rules.wizard.whens.WhenWizardModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

public class WhenWizardOptionPane extends JOptionPane implements IFormComponent {

    private final JPanel container;
    private final WhenWizardModel model;
    private JTextField ruleName;
    private JPanel whenWizardItemsComponent;
    private final IEventListener<PropertyChangedArgs> whenWizardItemChangedListener = this::onWhenWizardItemChanged;

    private WhenWizardOptionPane(WhenWizardModel model) {
        super(new JPanel(new BorderLayout()), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        container = (JPanel)message;
        this.model = model;
        addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this::onPropertyChanged);
        initComponent();
    }

    private void onPropertyChanged(PropertyChangeEvent event) {
        if (getValue() != null && (int)getValue() == JOptionPane.OK_OPTION) {
            if (model.createRule()) {
                JOptionPane.showMessageDialog(this,
                        "Rule created. Navigate to Reshaper to finish the rule.",
                        "Rule Created",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", model.validate()),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            model.setDismissed(true);
        }
    }

    public static void showDialog(WhenWizardModel model) {
        WhenWizardOptionPane optionPane = new WhenWizardOptionPane(model);
        JDialog dialog = optionPane.createDialog("When");
        dialog.setResizable(true);

        dialog.setModal(false);
        dialog.setVisible(true);
    }

    private void initComponent() {
        container.add(getBody(), BorderLayout.CENTER);
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        ruleName = createTextField(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        scrollPane.setViewportView(container);

        JButton addItem = new JButton("Add");

        ruleName.getDocument().addDocumentListener(new DocumentActionListener(this::onRuleNameChanged));
        addItem.addActionListener(this::onAddItem);

        container.add(getLabeledField("Rule Name *", ruleName), "wrap");
        container.add(getWhenWizardItemList(), "wrap");
        container.add(getPaddedButton(addItem), "wrap");

        scrollPane.setPreferredSize(container.getPreferredSize());

        return scrollPane;
    }

    private void onRuleNameChanged(ActionEvent actionEvent) {
        model.setRuleName(ruleName.getText());
    }

    private void onAddItem(ActionEvent actionEvent) {
        onAddItem();
    }

    private void onAddItem() {
        boolean deletable = !model.getItems().isEmpty();
        WhenWizardItemModel itemModel = model.addItem()
                .withListener(whenWizardItemChangedListener);
        whenWizardItemsComponent.add(new WhenWizardItemComponent(
                itemModel,
                deletable
        ), "wrap");
        whenWizardItemsComponent.revalidate();
        whenWizardItemsComponent.repaint();
    }

    private JPanel getWhenWizardItemList() {
        whenWizardItemsComponent = new JPanel(new MigLayout());
        whenWizardItemsComponent.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        if (model.getItems().isEmpty()) {
            model.addItem();
        }

        boolean deletableItem = false;
        for (WhenWizardItemModel item : model.getItems()) {
            item.withListener(whenWizardItemChangedListener);
            whenWizardItemsComponent.add(new WhenWizardItemComponent(item, deletableItem), "wrap");
            deletableItem = true;
        }
        return whenWizardItemsComponent;
    }

    private void onWhenWizardItemChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("deleted") && (boolean)propertyChangedArgs.getValue()) {
            WhenWizardItemModel whenWizardItemModel = (WhenWizardItemModel)propertyChangedArgs.getSource();
            removeWhenWizardItem(whenWizardItemModel);
        }
    }

    private void removeWhenWizardItem(WhenWizardItemModel whenWizardItemModel) {
        int index = model.removeItem(whenWizardItemModel);
        whenWizardItemsComponent.remove(index);
        whenWizardItemsComponent.revalidate();
        whenWizardItemsComponent.repaint();
    }
}
