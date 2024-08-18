package synfron.reshaper.burp.ui.components.workspaces;

import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;

import javax.swing.*;
import java.awt.*;

public interface IWorkspaceDependentComponent {
    default <T extends Component> Workspace getHostedWorkspace(T component) {
        Workspace workspace = Workspaces.get().getCurrentWorkspace();
        if (workspace == null) {
            IWorkspaceHost workspaceHost = (IWorkspaceHost) SwingUtilities.getAncestorOfClass(IWorkspaceHost.class, component);
            if (workspaceHost != null) {
                workspace = workspaceHost.getWorkspace();
            }
        }
        return workspace;
    }
}
