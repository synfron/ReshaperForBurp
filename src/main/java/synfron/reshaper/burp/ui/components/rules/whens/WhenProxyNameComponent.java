package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.rules.whens.WhenProxyName;
import synfron.reshaper.burp.ui.models.rules.whens.WhenProxyNameModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenProxyNameComponent extends WhenComponent<WhenProxyNameModel, WhenProxyName> {
    private JTextField proxyName;

    public WhenProxyNameComponent(WhenProxyNameModel when) {
        super(when);
        initComponent();
    }

    private void initComponent() {
        proxyName = new JTextField();
        JButton validate = new JButton("Validate");

        proxyName.setText(model.getProxyName());

        proxyName.getDocument().addDocumentListener(new DocumentActionListener(this::onProxyNameChanged));
        validate.addActionListener(this::onValidate);

        mainContainer.add(getLabeledField("Proxy Name", proxyName), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
        mainContainer.add(getPaddedButton(validate));
    }

    private void onProxyNameChanged(ActionEvent actionEvent) {
        model.setProxyName(proxyName.getText());
    }
}
