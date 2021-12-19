package synfron.reshaper.burp.ui.components.rules.thens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.rules.thens.ThenRunScript;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunScriptModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ThenRunScriptComponent extends ThenComponent<ThenRunScriptModel, ThenRunScript> {
    private JTextPane script;
    private JTextField maxExecutionSeconds;

    public ThenRunScriptComponent(ThenRunScriptModel then) {
        super(then);
        initComponent();
    }

    private void initComponent() {
        mainContainer.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        script = new JTextPane();
        scrollPane.setViewportView(script);

        script.setText(model.getScript());

        script.getDocument().addDocumentListener(new DocumentActionListener(this::onScriptChanged));

        mainContainer.add(new JLabel("Script *"), BorderLayout.PAGE_START);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(getOtherFields(), BorderLayout.PAGE_END);
    }

    private Component getOtherFields() {
        JPanel container = new JPanel(new MigLayout());

        maxExecutionSeconds = new JTextField();

        maxExecutionSeconds.setText(model.getMaxExecutionSeconds());

        maxExecutionSeconds.getDocument().addDocumentListener(new DocumentActionListener(this::onMaxExecutionSecondsChanged));

        container.add(getLabeledField("Max Execution (secs) *", maxExecutionSeconds), "wrap");
        container.add(getPaddedButton(validate));
        return container;
    }

    private void onScriptChanged(ActionEvent actionEvent) {
        model.setScript(script.getText());

    }

    private void onMaxExecutionSecondsChanged(ActionEvent actionEvent) {
        model.setMaxExecutionSeconds(maxExecutionSeconds.getText());
    }
}
