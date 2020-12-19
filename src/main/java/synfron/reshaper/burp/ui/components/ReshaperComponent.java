/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components;

import burp.ITab;
import synfron.reshaper.burp.ui.components.rules.RulesTabComponent;
import synfron.reshaper.burp.ui.components.settings.SettingsTabComponent;
import synfron.reshaper.burp.ui.components.vars.VariablesTabComponent;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author AssistantX
 */
public class ReshaperComponent extends JPanel implements ITab {

    public ReshaperComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(getTabs());
    }

    private JTabbedPane getTabs() {
        JTabbedPane tabs = new JTabbedPane();

        RulesTabComponent rules = new RulesTabComponent();
        VariablesTabComponent variables = new VariablesTabComponent();
        SettingsTabComponent settings = new SettingsTabComponent();

        tabs.addTab("Rules", rules);
        tabs.addTab("Global Variables", variables);
        tabs.addTab("Settings", settings);
        return tabs;
    }

    @Override
    public String getTabCaption() {
        return "Reshaper";
    }

    @Override
    public Component getUiComponent() {
        return this;
    }
}
