/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components;

import burp.BurpExtender;
import synfron.reshaper.burp.core.Tab;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.rules.RulesTabComponent;
import synfron.reshaper.burp.ui.components.settings.SettingsTabComponent;
import synfron.reshaper.burp.ui.components.vars.VariablesTabComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.HashMap;

public class ReshaperComponent extends JPanel {

    private final IEventListener<PropertyChangedArgs> generalSettingsChangedListener = this::onGeneralSettingsChanged;
    private JTabbedPane tabs;

    public ReshaperComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(getTabs());

        BurpExtender.getGeneralSettings().withListener(generalSettingsChangedListener);
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
        HashMap<Tab, Component> existingTabs = new HashMap<>();
        for (int tabIndex = 0; tabIndex < tabs.getTabCount(); tabIndex++) {
            Tab tab = Tab.byName(tabs.getTitleAt(tabIndex));
            existingTabs.put(tab, tabs.getComponentAt(tabIndex));
        }
        Component activeTab = tabs.getSelectedComponent();
        tabs.removeAll();

        for (Tab tab : Tab.values()) {
            if (!tab.isHideable() || !BurpExtender.getGeneralSettings().getHiddenTabs().contains(tab)) {
                Component component = existingTabs.get(tab);
                if (component == null) {
                    component = switch (tab) {
                        case HttpRules -> new RulesTabComponent(ProtocolType.Http);
                        case WebSocketRules -> new RulesTabComponent(ProtocolType.WebSocket);
                        case GlobalVariables -> new VariablesTabComponent();
                        case Logs -> new LogsComponent();
                        case Settings -> new SettingsTabComponent();
                    };
                }
                tabs.addTab(tab.toString(), component);
                if (activeTab == component) {
                    tabs.setSelectedComponent(component);
                }
            }
        }
    }
}
