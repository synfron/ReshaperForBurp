package synfron.reshaper.burp.ui.utils;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.Variables;
import synfron.reshaper.burp.ui.components.rules.wizard.whens.WhenWizardComponent;
import synfron.reshaper.burp.ui.models.rules.wizard.whens.WhenWizardModel;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ContextMenuHandler implements ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem("Create Rule");
        menuItem.addActionListener(actionEvent -> onCreateHttpRule(event.selectedRequestResponses()));
        return event.selectedRequestResponses().size() == 1 ? Collections.singletonList(menuItem) : Collections.emptyList();
    }

    @Override
    public List<Component> provideMenuItems(WebSocketContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem("Create Rule");
        menuItem.addActionListener(actionEvent -> onCreateWebSocketRule(event.selectedWebSocketMessages()));
        return event.selectedWebSocketMessages().size() == 1 ? Collections.singletonList(menuItem) : Collections.emptyList();
    }

    private void onCreateWebSocketRule(List<WebSocketMessage> selectedItems) {
        WebSocketMessage webSocketMessage = selectedItems.getFirst();
        openWhenWizard(new WhenWizardModel(new WebSocketEventInfo<>(Workspaces.get().getDefault(), WebSocketMessageType.Binary, WebSocketDataDirection.from(webSocketMessage.direction()), null, null, webSocketMessage.upgradeRequest(), webSocketMessage.annotations(), webSocketMessage.payload().getBytes(), new Variables())));
    }

    private void onCreateHttpRule(List<HttpRequestResponse> selectedItems) {
        HttpRequestResponse httpRequestResponse = selectedItems.getFirst();
        openWhenWizard(new WhenWizardModel(new HttpEventInfo(Workspaces.get().getDefault(), null, null, null, httpRequestResponse.request(), httpRequestResponse.response(), httpRequestResponse.annotations(), new Variables())));
    }

    private void openWhenWizard(WhenWizardModel model) {
        try {
            ModalPrompter.open(model, new ModalPrompter.FormPromptArgs<>(
                "When",
                model,
                new WhenWizardComponent(model)
            ));
        } catch (Exception e) {
            Log.get(Workspaces.get().getDefault()).withMessage("Failed to create rule from content menu").withException(e).logErr();
        }
    }
}
