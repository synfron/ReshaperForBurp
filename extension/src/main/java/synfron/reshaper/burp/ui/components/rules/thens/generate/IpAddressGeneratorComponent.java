package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IIpAddressGeneratorModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IpAddressGeneratorComponent extends GeneratorComponent<IIpAddressGeneratorModel> {
    
    private JComboBox<ValueGenerator.IpVersion> version;

    public IpAddressGeneratorComponent(IIpAddressGeneratorModel model, boolean allowVariableTags) {
        super(model, allowVariableTags);
    }

    protected void initComponent() {
        version = createComboBox(ValueGenerator.IpVersion.values());

        version.setSelectedItem(model.getVersion());

        version.addActionListener(this::onVersionChanged);

        add(getLabeledField("Version", version), "wrap");
    }

    private void onVersionChanged(ActionEvent actionEvent) {
        model.setVersion((ValueGenerator.IpVersion)version.getSelectedItem());
    }
}
