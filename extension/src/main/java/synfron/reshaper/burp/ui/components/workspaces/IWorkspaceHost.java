package synfron.reshaper.burp.ui.components.workspaces;

import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;

import javax.swing.*;
import java.util.function.Supplier;

public interface IWorkspaceHost {
    Workspace getWorkspace();

    default void createEntryPoint(Runnable job) {
        Workspace workspace = getWorkspace();
        Workspaces.get().setCurrentWorkspace(workspace);
        job.run();
    }

    default void createInvokeLaterEntryPoint(Runnable job) {
        Workspace workspace = getWorkspace();
        SwingUtilities.invokeLater(() -> {
            Workspaces.get().setCurrentWorkspace(workspace);
            job.run();
        });
    }

    default <T> T createEntryPoint(Supplier<T> job) {
        Workspace workspace = getWorkspace();
        Workspaces.get().setCurrentWorkspace(workspace);
        return job.get();
    }
}
