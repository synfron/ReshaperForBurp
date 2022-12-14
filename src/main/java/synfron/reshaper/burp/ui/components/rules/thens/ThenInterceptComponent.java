package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.InterceptResponse;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenIntercept;
import synfron.reshaper.burp.ui.models.rules.thens.ThenInterceptModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ThenInterceptComponent extends ThenComponent<ThenInterceptModel, ThenIntercept> {
    private JComboBox<InterceptResponse> interceptResponse;

    public ThenInterceptComponent(ProtocolType protocolType, ThenInterceptModel then) {
        super(protocolType, then);
        initComponent();
    }

    private void initComponent() {
        interceptResponse = createComboBox(ThenIntercept.getSupportedResponses());

        interceptResponse.setSelectedItem(model.getInterceptResponse());

        interceptResponse.addActionListener(this::onInterceptResponseChanged);

        mainContainer.add(getLabeledField("Action", interceptResponse), "wrap");
        mainContainer.add(getPaddedButton(validate));
    }

    private void onInterceptResponseChanged(ActionEvent actionEvent) {
        model.setInterceptResponse((InterceptResponse)interceptResponse.getSelectedItem());
    }
}
