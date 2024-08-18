/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components.workspaces;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.WorkspaceTab;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.ui.components.LogsComponent;
import synfron.reshaper.burp.ui.components.rules.RulesTabComponent;
import synfron.reshaper.burp.ui.components.settings.SettingsTabComponent;
import synfron.reshaper.burp.ui.components.vars.VariablesTabComponent;
import synfron.reshaper.burp.ui.utils.UiMessageHandler;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class WorkspaceComponent extends JPanel implements IWorkspaceHost {

    private final IEventListener<PropertyChangedArgs> generalSettingsChangedListener = this::onGeneralSettingsChanged;
    @Getter
    private final Workspace workspace;
    private JTabbedPane tabs;
    private final UiMessageHandler uiMessageHandler;

    public WorkspaceComponent(Workspace workspace) {
        this.workspace = workspace;
        this.uiMessageHandler = new UiMessageHandler(workspace.getMessageEvent());
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(getTabs());

        workspace.getGeneralSettings().withListener(generalSettingsChangedListener);
    }

    private JTabbedPane getTabs() {
        tabs = new JTabbedPane();

        addOrRemoveTabs();
        return tabs;
    }

    private void onGeneralSettingsChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("hiddenTabs")) {
            addOrRemoveTabs();
        }
    }

    private void addOrRemoveTabs() {
        Workspaces.get().setCurrentWorkspace(workspace);
        HashMap<WorkspaceTab, Component> existingTabs = new HashMap<>();
        for (int tabIndex = 0; tabIndex < tabs.getTabCount(); tabIndex++) {
            WorkspaceTab tab = WorkspaceTab.byName(tabs.getTitleAt(tabIndex));
            existingTabs.put(tab, tabs.getComponentAt(tabIndex));
        }
        Component activeTab = tabs.getSelectedComponent();
        tabs.removeAll();

        for (WorkspaceTab tab : WorkspaceTab.values()) {
            if (!tab.isHideable() || !workspace.getGeneralSettings().getHiddenTabs().contains(tab)) {
                Component component = existingTabs.get(tab);
                if (component == null) {
                    component = createEntryPoint(() -> switch (tab) {
                        case HttpRules -> new RulesTabComponent(ProtocolType.Http);
                        case WebSocketRules -> new RulesTabComponent(ProtocolType.WebSocket);
                        case GlobalVariables -> new VariablesTabComponent();
                        case Logs -> new LogsComponent();
                        case Settings -> new SettingsTabComponent();
                    });
                }
                tabs.addTab(tab.toString(), component);
                if (activeTab == component) {
                    tabs.setSelectedComponent(component);
                }
            }
        }
    }
}
