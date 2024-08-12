/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.runner;

import burp.BurpExtender;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.BurpUtils;
import synfron.reshaper.burp.ui.components.ReshaperComponent;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public static void main(String[] args) {
        BurpUtils.current = new Api.BurpUtilsImpl();
        new BurpExtender().initialize(new Api());
        new Window().setVisible(true);
    }

    public Window() {
        super("Reshaper");
        initComponents();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dimension.width / 2, dimension.height / 2);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        add(new ReshaperComponent(Workspaces.get()));
    }

}
