package synfron.reshaper.burp.ui.components.rules.thens;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRunScript;
import synfron.reshaper.burp.ui.models.rules.thens.ThenRunScriptModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ThenRunScriptComponent extends ThenComponent<ThenRunScriptModel, ThenRunScript> {
    private JTextArea script;
    private JTextField maxExecutionSeconds;

    public ThenRunScriptComponent(ProtocolType protocolType, ThenRunScriptModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainContainer.setLayout(new BorderLayout());
        script = createTextComponent(new JTextArea());
        script.setLineWrap(true);

        script.setText(model.getScript());

        script.getDocument().addDocumentListener(new DocumentActionListener(this::onScriptChanged));

        mainContainer.add(getLabeledField("Script *", script, true), BorderLayout.CENTER);
        mainContainer.add(getOtherFields(), BorderLayout.PAGE_END);
    }

    private Component getOtherFields() {
        JPanel container = new JPanel(new MigLayout());

        maxExecutionSeconds = createTextField(false);

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
