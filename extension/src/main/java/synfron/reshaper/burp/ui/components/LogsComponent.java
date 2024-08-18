package synfron.reshaper.burp.ui.components;

import burp.BurpExtender;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.message.LogMessage;
import synfron.reshaper.burp.core.events.message.MessageType;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.utils.BurpUtils;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceDependent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;

public class LogsComponent extends JPanel implements IWorkspaceDependent {

    @Getter
    private final WebSocketMessageEditor textEditor = BurpExtender.getApi().userInterface().createWebSocketMessageEditor(EditorOptions.READ_ONLY);
    private final Workspace workspace;
    private final IEventListener<MessageArgs> onMessageListener = this::onMessage;

    public LogsComponent() {
        this.workspace = getHostedWorkspace();
        initComponents();

        workspace.getMessageEvent().add(onMessageListener);
    }

    private void onMessage(MessageArgs messageArgs) {
        if (messageArgs.getData().getMessageType() == MessageType.Log && messageArgs.getData() instanceof LogMessage logMessage) {
            textEditor.setContents(BurpUtils.current.toByteArray(
                    TextUtils.bufferAppend(
                            new String(textEditor.getContents().getBytes(), StandardCharsets.UTF_8),
                            logMessage.getText(),
                            "\n",
                            workspace.getGeneralSettings().getLogTabCharacterLimit()
                    ).getBytes(StandardCharsets.UTF_8)
            ));
        }
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
        textEditor.setContents(BurpUtils.current.toByteArray(new byte[0]));
    }
}
