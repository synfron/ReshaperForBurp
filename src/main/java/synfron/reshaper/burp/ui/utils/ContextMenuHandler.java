package synfron.reshaper.burp.ui.utils;

import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.rules.wizard.whens.WhenWizardOptionPane;
import synfron.reshaper.burp.ui.models.rules.wizard.whens.WhenWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class ContextMenuHandler implements IContextMenuFactory {

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        JMenuItem menuItem = new JMenuItem("Create Rule");
        menuItem.addActionListener(actionEvent -> onCreateRule(invocation, actionEvent));
        return invocation.getSelectedMessages().length == 1 ? Collections.singletonList(menuItem) : Collections.emptyList();
    }

    private void onCreateRule(IContextMenuInvocation invocation, ActionEvent actionEvent) {
        openWhenWizard(new WhenWizardModel(new EventInfo(null, null, invocation.getSelectedMessages()[0])));
    }

    private void openWhenWizard(WhenWizardModel model) {
        try {
            ModalPrompter.open(model, ignored -> WhenWizardOptionPane.showDialog(model), true);
        } catch (Exception e) {
            Log.get().withMessage("Failed to create rule from content menu").withException(e).logErr();
        }
    }
}
