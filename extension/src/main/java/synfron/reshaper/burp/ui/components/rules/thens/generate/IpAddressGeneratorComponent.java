package synfron.reshaper.burp.ui.components.rules.thens.generate;

import synfron.reshaper.burp.core.rules.thens.entities.generate.IpAddressGenerator;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IpAddressGeneratorModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IpAddressGeneratorComponent extends GeneratorComponent<IpAddressGeneratorModel> {
    
    private JComboBox<IpAddressGenerator.IpVersion> version;

    public IpAddressGeneratorComponent(IpAddressGeneratorModel model) {
        super(model);
    }

    protected void initComponent() {
        version = createComboBox(IpAddressGenerator.IpVersion.values());

        version.setSelectedItem(model.getVersion());

        version.addActionListener(this::onVersionChanged);

        add(getLabeledField("Version", version), "wrap");
    }

    private void onVersionChanged(ActionEvent actionEvent) {
        model.setVersion((IpAddressGenerator.IpVersion)version.getSelectedItem());
    }
}
