package synfron.reshaper.burp.ui.components.settings;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.WorkspaceTab;
import synfron.reshaper.burp.core.rules.thens.ThenType;
import synfron.reshaper.burp.core.rules.whens.WhenType;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.settings.HideItemsModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.stream.Stream;

public class HideItemsComponent extends JPanel implements IFormComponent {

    private final HideItemsModel model;

    public HideItemsComponent(HideItemsModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {
        add(getBody(), BorderLayout.CENTER);
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
        WorkspaceTab item = (WorkspaceTab) checkbox.getClientProperty("item");
        
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
        addCheckboxes(hideTabs, 4, Stream.of(WorkspaceTab.values()).filter(WorkspaceTab::isHideable).toList(), model.getHiddenTabs(), this::onHiddenTabChanged);

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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
