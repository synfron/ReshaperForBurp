package synfron.reshaper.burp.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

public class ForegroundColorListCellRenderer extends DefaultListCellRenderer {

    private final BiFunction<Object, Color, Color> colorProvider;

    public ForegroundColorListCellRenderer(BiFunction<Object, Color, Color> colorProvider) {
        this.colorProvider = colorProvider;
        putClientProperty("html.disable", Boolean.TRUE);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Color newColor = colorProvider.apply(value, cell.getForeground());
        if (newColor != null) {
            cell.setForeground(newColor);
        }
        return cell;
    }
}
