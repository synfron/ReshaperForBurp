/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.components.rules.RulesTabComponent;
import synfron.reshaper.burp.ui.components.settings.SettingsTabComponent;
import synfron.reshaper.burp.ui.components.vars.VariablesTabComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class ReshaperComponent extends JPanel {

    private JTabbedPane tabs;

    public ReshaperComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(getTabs());
    }

    private JTabbedPane getTabs() {
        tabs = new JTabbedPane();

        tabs.addTab("HTTP Rules", new RulesTabComponent(ProtocolType.Http));
        tabs.addTab("WebSocket Rules", new RulesTabComponent(ProtocolType.WebSocket));
        tabs.addTab("Global Variables", new VariablesTabComponent());
        tabs.addTab("Logs", new LogsComponent());
        tabs.addTab("Settings", new SettingsTabComponent());

        tabs.addChangeListener(this::onTabChanged);
        return tabs;
    }

    private void onTabChanged(ChangeEvent e) {
        if (tabs.getSelectedIndex() >= 0) {
            Component selectedTab = tabs.getComponentAt(tabs.getSelectedIndex());
            if (selectedTab instanceof ITabActivationListener listener) {
                listener.onActivated();
            }
        }
    }
}
