package synfron.reshaper.burp.ui.utils;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.rules.wizard.whens.WhenWizardOptionPane;
import synfron.reshaper.burp.ui.models.rules.wizard.whens.WhenWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class ContextMenuHandler implements ContextMenuItemsProvider {

    @Override
    public List<JMenuItem> provideMenuItems(ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem("Create Rule");
        menuItem.addActionListener(actionEvent -> onCreateRule(event.selectedRequestResponses(), actionEvent));
        return event.selectedRequestResponses().size() == 1 ? Collections.singletonList(menuItem) : Collections.emptyList();
    }

    private void onCreateRule(List<HttpRequestResponse> selectedItems, ActionEvent actionEvent) {
        HttpRequestResponse httpRequestResponse = selectedItems.get(0);
        openWhenWizard(new WhenWizardModel(new HttpEventInfo(null, null, httpRequestResponse.httpRequest(), httpRequestResponse.httpResponse(), httpRequestResponse.messageAnnotations())));
    }

    private void openWhenWizard(WhenWizardModel model) {
        try {
            ModalPrompter.open(model, ignored -> WhenWizardOptionPane.showDialog(model), true);
        } catch (Exception e) {
            Log.get().withMessage("Failed to create rule from content menu").withException(e).logErr();
        }
    }
}
