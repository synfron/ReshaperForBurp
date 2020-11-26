/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components;

import burp.ITab;
import synfron.reshaper.burp.ui.components.rules.RulesTabComponent;
import synfron.reshaper.burp.ui.components.vars.VariablesTabComponent;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author AssistantX
 */
public class ReshaperComponent extends JPanel implements ITab {

    private RulesTabComponent rules;
    private VariablesTabComponent variables;

    public ReshaperComponent() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(getTabs());
    }

    private JTabbedPane getTabs() {
        JTabbedPane tabs = new JTabbedPane();

        rules = new RulesTabComponent();
        variables = new VariablesTabComponent();

        tabs.addTab("Rules", rules);
        tabs.addTab("Variables", variables);
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
