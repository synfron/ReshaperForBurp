package synfron.reshaper.burp.ui.utils;

import javax.swing.table.DefaultTableCellRenderer;

public class TableCellRenderer extends DefaultTableCellRenderer {

    public TableCellRenderer() {
        putClientProperty("html.disable", Boolean.TRUE);
    }
}
