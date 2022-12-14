package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenProxyName;
import synfron.reshaper.burp.ui.models.rules.whens.WhenProxyNameModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenProxyNameComponent extends WhenComponent<WhenProxyNameModel, WhenProxyName> {
    private JTextField proxyName;

    public WhenProxyNameComponent(ProtocolType protocolType, WhenProxyNameModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        proxyName = createTextField(false);

        proxyName.setText(model.getProxyName());

        proxyName.getDocument().addDocumentListener(new DocumentActionListener(this::onProxyNameChanged));

        mainContainer.add(getLabeledField("Proxy Name *", proxyName), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onProxyNameChanged(ActionEvent actionEvent) {
        model.setProxyName(proxyName.getText());
    }
}
