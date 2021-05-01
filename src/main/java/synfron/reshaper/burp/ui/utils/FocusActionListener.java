package synfron.reshaper.burp.ui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusActionListener implements FocusListener {
    private final ActionListener actionListener;

    public FocusActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void focusGained(FocusEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getSource(), FocusEvent.FOCUS_GAINED, null));
    }

    @Override
    public void focusLost(FocusEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getSource(), FocusEvent.FOCUS_LOST, null));
    }
}
