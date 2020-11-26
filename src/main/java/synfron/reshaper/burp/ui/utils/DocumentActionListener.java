package synfron.reshaper.burp.ui.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DocumentActionListener implements DocumentListener {
    private final ActionListener actionListener;

    public DocumentActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getDocument(), ActionEvent.ACTION_PERFORMED, null));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getDocument(), ActionEvent.ACTION_PERFORMED, null));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getDocument(), ActionEvent.ACTION_PERFORMED, null));
    }
}
