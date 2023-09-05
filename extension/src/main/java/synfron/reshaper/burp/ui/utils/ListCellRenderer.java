package synfron.reshaper.burp.ui.utils;

import javax.swing.*;

public class ListCellRenderer extends DefaultListCellRenderer {

    public ListCellRenderer() {
        putClientProperty("html.disable", Boolean.TRUE);
    }
}
