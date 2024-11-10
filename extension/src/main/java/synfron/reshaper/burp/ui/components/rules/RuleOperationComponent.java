package synfron.reshaper.burp.ui.components.rules;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceDependentComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;

import javax.swing.*;
import java.awt.*;

public abstract class RuleOperationComponent<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> extends JScrollPane implements IFormComponent, IWorkspaceDependentComponent {

    @Getter
    protected final ProtocolType protocolType;
    @Getter
    protected final P model;
    protected final JPanel mainContainer;
    @Getter
    private final Workspace workspace;

    protected RuleOperationComponent(ProtocolType protocolType, P model) {
        this.workspace = getHostedWorkspace();
        this.protocolType = protocolType;
        this.model = model;
        mainContainer = new JPanel(new MigLayout());
        setViewportView(mainContainer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }

}
