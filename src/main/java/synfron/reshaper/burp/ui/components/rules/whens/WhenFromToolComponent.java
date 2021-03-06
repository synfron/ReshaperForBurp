package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.rules.whens.WhenFromTool;
import synfron.reshaper.burp.ui.models.rules.whens.WhenFromToolModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenFromToolComponent extends WhenComponent<WhenFromToolModel, WhenFromTool> {
    private JComboBox<BurpTool> tool;

    public WhenFromToolComponent(WhenFromToolModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        tool = new JComboBox<>(BurpTool.values());
        JButton save = new JButton("Save");

        tool.setSelectedItem(model.getTool());

        tool.addActionListener(this::onToolChanged);
        save.addActionListener(this::onSave);

        mainContainer.add(getLabeledField("Tool", tool), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(save));
    }

    private void onToolChanged(ActionEvent actionEvent) {
        model.setTool((BurpTool)tool.getSelectedItem());
    }
}
