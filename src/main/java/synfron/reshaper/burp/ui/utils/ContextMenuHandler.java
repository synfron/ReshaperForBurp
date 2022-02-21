package synfron.reshaper.burp.ui.utils;

import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IHttpRequestResponse;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.rules.wizard.WhenWizardOptionPane;
import synfron.reshaper.burp.ui.models.rules.wizard.WhenWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class ContextMenuHandler implements IContextMenuFactory {

    private final IEventListener<PropertyChangedArgs> whenWizardChangedListener = this::onWhenWizardChanged;

    private void onWhenWizardChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("invalidated") && (boolean)propertyChangedArgs.getValue()) {
            WhenWizardModel whenWizardModel = (WhenWizardModel)propertyChangedArgs.getSource();
            openWhenWizard(whenWizardModel);
        }
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        JMenuItem menuItem = new JMenuItem("Create Rule");
        menuItem.addActionListener(actionEvent -> onCreateRule(invocation, actionEvent));
        return invocation.getSelectedMessages().length == 1 ? Collections.singletonList(menuItem) : Collections.emptyList();
    }

    private void onCreateRule(IContextMenuInvocation invocation, ActionEvent actionEvent) {
        IHttpRequestResponse requestResponse = invocation.getSelectedMessages()[0];
        IEventInfo eventInfo = new EventInfo(null, null, requestResponse);
        WhenWizardModel model = new WhenWizardModel(eventInfo);
        openWhenWizard(model);
    }

    private void openWhenWizard(WhenWizardModel model) {
        try {
            model.resetPropertyChangedListener();
            model.withListener(whenWizardChangedListener);
            WhenWizardOptionPane.showDialog(model);
        } catch (Exception e) {
            Log.get().withMessage("Failed to create rule from content menu").withException(e).logErr();
        }
    }
}
