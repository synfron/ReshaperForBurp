package synfron.reshaper.burp.ui.components;

import burp.BurpExtender;
import burp.ITextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LogsComponent extends JPanel {

    private final ITextEditor textEditor = BurpExtender.getLogTextEditor();

    public LogsComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        add(textEditor.getComponent(), BorderLayout.CENTER);
        add(getActionBar(), BorderLayout.PAGE_END);
    }

    private Component getActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton clear = new JButton("Clear");

        clear.addActionListener(this::onClear);

        actionBar.add(clear);
        return actionBar;
    }

    private void onClear(ActionEvent actionEvent) {
        textEditor.setText(new byte[0]);
    }
}
