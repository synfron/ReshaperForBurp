package synfron.reshaper.burp.ui.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionPerformedListener extends AbstractAction {

    private final ActionListener actionListener;

    public ActionPerformedListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionListener.actionPerformed(e);
    }
}
