package synfron.reshaper.burp.ui.components.workspaces;

import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;

public interface IWorkspaceDependent {
    default Workspace getHostedWorkspace() {
        return Workspaces.get().getCurrentWorkspace();
    }
}
