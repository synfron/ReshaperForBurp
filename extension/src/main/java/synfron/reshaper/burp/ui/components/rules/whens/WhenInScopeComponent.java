package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenInScope;
import synfron.reshaper.burp.ui.models.rules.whens.WhenInScopeModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WhenInScopeComponent extends WhenComponent<WhenInScopeModel, WhenInScope> {
    private JTextField url;

    public WhenInScopeComponent(ProtocolType protocolType, WhenInScopeModel when) {
        super(protocolType, when);
        initComponent();
    }

    private void initComponent() {
        url = createTextField(true);

        url.setText(model.getUrl());

        url.getDocument().addDocumentListener(new DocumentActionListener(this::onUrlChanged));

        mainContainer.add(getLabeledField("URL", url), "wrap");
        getDefaultComponents().forEach(component -> mainContainer.add(component, "wrap"));
    }

    private void onUrlChanged(ActionEvent actionEvent) {
        model.setUrl(url.getText());
    }
}
