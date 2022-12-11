package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenFromTool;
import synfron.reshaper.burp.ui.models.rules.whens.WhenFromToolModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenFromToolComponent extends WhenComponent<WhenFromToolModel, WhenFromTool> {
    private JComboBox<BurpTool> tool;

    public WhenFromToolComponent(ProtocolType protocolType, WhenFromToolModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        tool = createComboBox(BurpTool.values());

        tool.setSelectedItem(model.getTool());

        tool.addActionListener(this::onToolChanged);

        mainContainer.add(getLabeledField("Tool", tool), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onToolChanged(ActionEvent actionEvent) {
        model.setTool((BurpTool)tool.getSelectedItem());
    }
}
