package synfron.reshaper.burp.ui.components.settings;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.Tab;
import synfron.reshaper.burp.core.rules.thens.ThenType;
import synfron.reshaper.burp.core.rules.whens.WhenType;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.settings.HideItemsModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.stream.Stream;

public class HideItemsOptionPane extends JOptionPane implements IFormComponent {

    private final JPanel container;
    private final HideItemsModel model;

    private HideItemsOptionPane(HideItemsModel model) {
        super(new JPanel(new BorderLayout()), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        container = (JPanel)message;
        this.model = model;
        addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this::onPropertyChanged);
        initComponent();
    }

    private void onPropertyChanged(PropertyChangeEvent event) {
        if (getValue() != null && (int)getValue() == JOptionPane.OK_OPTION) {
            model.save();
        } else {
            model.setDismissed(true);
        }
    }

    public static void showDialog(HideItemsModel model) {
        HideItemsOptionPane optionPane = new HideItemsOptionPane(model);
        JDialog dialog = optionPane.createDialog("Hide Features");
        dialog.setResizable(true);

        dialog.setModal(false);
        dialog.setVisible(true);
    }

    private void initComponent() {
        container.add(getBody(), BorderLayout.CENTER);
    }
    
    private <T> void addCheckboxes(JPanel container, int maxRowLength, java.util.List<T> items, HashSet<T> checkedItems, ActionListener checkboxChangedListener) {
        int rowItemCount = 1;
        int totalCount = 0;
        for (T item : items) {
            totalCount++;
            JCheckBox checkbox = new JCheckBox(item.toString());
            checkbox.putClientProperty("item", item);

            checkbox.setSelected(checkedItems.contains(item));

            checkbox.addActionListener(checkboxChangedListener);

            container.add(checkbox, rowItemCount == maxRowLength || totalCount == items.size()  ? "wrap" : "");
            
            if (rowItemCount < maxRowLength) {
                rowItemCount++;
            } else {
                rowItemCount = 1;
            }
        }
    }

    private void onHiddenTabChanged(ActionEvent actionEvent) {
        JCheckBox checkbox = (JCheckBox) actionEvent.getSource();
        Tab item = (Tab) checkbox.getClientProperty("item");
        
        if (checkbox.isSelected()) {
            model.addHiddenTab(item);
        } else {
            model.removeHiddenTab(item);
        }
    }

    private void onHiddenWhenTypeChanged(ActionEvent actionEvent) {
        JCheckBox checkbox = (JCheckBox) actionEvent.getSource();
        String item = (String) checkbox.getClientProperty("item");

        if (checkbox.isSelected()) {
            model.addHiddenWhenType(item);
        } else {
            model.removeHiddenWhenType(item);
        }
    }

    private void onHiddenThenTypeChanged(ActionEvent actionEvent) {
        JCheckBox checkbox = (JCheckBox) actionEvent.getSource();
        String item = (String) checkbox.getClientProperty("item");

        if (checkbox.isSelected()) {
            model.addHiddenThenType(item);
        } else {
            model.removeHiddenThenType(item);
        }
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        JPanel hideTabs = new JPanel(new MigLayout());
        hideTabs.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        hideTabs.add(new JLabel("Hide Tabs:"), "wrap");
        addCheckboxes(hideTabs, 4, Stream.of(Tab.values()).filter(Tab::isHideable).toList(), model.getHiddenTabs(), this::onHiddenTabChanged);

        JPanel hideWhenTypes = new JPanel(new MigLayout());
        hideWhenTypes.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        hideWhenTypes.add(new JLabel("Hide When Type Options:"), "wrap");
        addCheckboxes(hideWhenTypes, 4, WhenType.getTypes().stream().map(WhenType::getName).sorted().distinct().toList(), model.getHiddenWhenTypes(), this::onHiddenWhenTypeChanged);


        JPanel hideThenTypes = new JPanel(new MigLayout());
        hideThenTypes.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        hideThenTypes.add(new JLabel("Hide Then Type Options:"), "wrap");
        addCheckboxes(hideThenTypes, 4, ThenType.getTypes().stream().map(ThenType::getName).sorted().distinct().toList(), model.getHiddenThenTypes(), this::onHiddenThenTypeChanged);

        container.add(hideTabs, "wrap");
        container.add(hideWhenTypes, "wrap");
        container.add(hideThenTypes, "wrap");

        return container;
    }
}
