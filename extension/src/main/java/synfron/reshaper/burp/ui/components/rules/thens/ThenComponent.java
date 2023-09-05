package synfron.reshaper.burp.ui.components.rules.thens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.ui.components.rules.RuleOperationComponent;
import synfron.reshaper.burp.ui.models.rules.thens.ThenModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

public abstract class ThenComponent<P extends ThenModel<P, T>, T extends Then<T>> extends RuleOperationComponent<P, T> {

    public ThenComponent(ProtocolType protocolType, P model) {
        super(protocolType, model);
        setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(String.format("Then %s", model.getType().getName())),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );
    }

}
