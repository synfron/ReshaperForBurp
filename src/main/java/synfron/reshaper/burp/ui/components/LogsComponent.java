package synfron.reshaper.burp.ui.components;

import burp.BurpExtender;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LogsComponent extends JPanel {

    private final WebSocketMessageEditor textEditor = BurpExtender.getLogTextEditor();

    public LogsComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        if (textEditor != null) {
            add(textEditor.uiComponent(), BorderLayout.CENTER);
            add(getActionBar(), BorderLayout.PAGE_END);
        }
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton clear = new JButton("Clear");

        clear.addActionListener(this::onClear);

        actionBar.add(clear);
        return actionBar;
    }

    private void onClear(ActionEvent actionEvent) {
        textEditor.setContents(ByteArray.byteArray(new byte[0]));
    }
}
