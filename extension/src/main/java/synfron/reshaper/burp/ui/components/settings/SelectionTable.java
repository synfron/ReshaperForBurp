package synfron.reshaper.burp.ui.components.settings;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.ui.utils.TableCellRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SelectionTable<T> extends JPanel {

    private Map<Object, Boolean> checkboxStateMap = new HashMap<>();
    private final JCheckBox selectAll;
    private final DefaultTableModel tableModel;


    public SelectionTable(String[] columnNames, boolean selectAll) {
        setLayout(new MigLayout());
        JTable table = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };
        tableModel = createTableModel(new Object[0][0], columnNames);
        table.setDefaultRenderer(Object.class, new TableCellRenderer());
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableModel.addTableModelListener(this::onTableChanged);

        this.selectAll = new JCheckBox("Select All");
        this.selectAll.setSelected(selectAll);
        this.selectAll.addActionListener(this::onSelectAllChanged);

        add(new JScrollPane(table), "wrap");
        add(this.selectAll, "gapbefore push");
    }

    private void onSelectAllChanged(ActionEvent actionEvent) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt(selectAll.isSelected(), row, 0);
        }
    }

    private void onTableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE && e.getFirstRow() >= 0) {
            Vector<?> row = tableModel.getDataVector().get(e.getFirstRow());
            boolean checkboxState = (Boolean) row.get(0);
            checkboxStateMap.put(row.get(1), checkboxState);
            if (!checkboxState && selectAll.isSelected()) {
                selectAll.setSelected(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getSelectedValues() {
        return tableModel.getDataVector().stream()
                .filter(row -> (boolean)row.get(0))
                .map(row -> (T)row.get(1))
                .toList();
    }

    public void setValues(List<T> values) {
        for (int row = tableModel.getRowCount() - 1; row >= 0; row--) {
            tableModel.removeRow(row);
        }
        Map<Object, Boolean> newCheckboxStateMap = new HashMap<>();
        boolean defaultCheckboxState = selectAll.isSelected();
        for (Object value : values) {
            boolean checkboxState = ObjectUtils.defaultIfNull(checkboxStateMap.get(value), defaultCheckboxState);
            tableModel.addRow(new Object[] { checkboxState, value });
            newCheckboxStateMap.put(value, checkboxState);
        }
        checkboxStateMap = newCheckboxStateMap;
    }

    private DefaultTableModel createTableModel(Object[][] data, Object[] columnNames) {
        return new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };
    }
}
