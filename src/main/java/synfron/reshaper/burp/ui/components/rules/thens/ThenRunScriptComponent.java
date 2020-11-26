package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.rules.thens.ThenRunScript;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunScriptModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ThenRunScriptComponent extends ThenComponent<ThenRunScriptModel, ThenRunScript> {
    private JTextArea script;

    public ThenRunScriptComponent(ThenRunScriptModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        mainContainer.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        script = new JTextArea();
        scrollPane.setViewportView(script);
        JButton save = new JButton("Save");

        script.setText(model.getScript());

        script.getDocument().addDocumentListener(new DocumentActionListener(this::onScriptChanged));
        save.addActionListener(this::onSave);

        mainContainer.add(new JLabel("Script"), BorderLayout.PAGE_START);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(getPaddedButton(save), BorderLayout.PAGE_END);
    }

    private void onScriptChanged(ActionEvent actionEvent) {
        model.setScript(script.getText());
    }
}
