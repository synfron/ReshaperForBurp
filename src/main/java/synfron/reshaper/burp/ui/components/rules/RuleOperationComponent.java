package synfron.reshaper.burp.ui.components.rules;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;

import javax.swing.*;

public abstract class RuleOperationComponent<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> extends JScrollPane implements IFormComponent {

    @Getter
    protected final ProtocolType protocolType;
    @Getter
    protected final P model;
    protected final JPanel mainContainer;

    protected RuleOperationComponent(ProtocolType protocolType, P model) {
        this.protocolType = protocolType;
        this.model = model;
        mainContainer = new JPanel(new MigLayout());
        setViewportView(mainContainer);
    }

}
