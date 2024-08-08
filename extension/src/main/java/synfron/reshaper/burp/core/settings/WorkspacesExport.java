package synfron.reshaper.burp.core.settings;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class WorkspacesExport implements Serializable {
    private UUID defaultWorkspaceUuid;
    private boolean enabled;
    private List<WorkspaceExport> workspaces = Collections.emptyList();

    public WorkspacesExport(Workspaces workspaces) {
        defaultWorkspaceUuid = workspaces.getDefaultWorkspaceUuid();
        enabled = workspaces.isEnabled();
        this.workspaces = workspaces.getWorkspaces().stream().map(WorkspaceExport::new).toList();
    }

    public Workspaces toWorkspaces() {
        Workspaces workspaces = new Workspaces();
        workspaces.setDefaultWorkspaceUuid(defaultWorkspaceUuid);
        workspaces.setEnabled(enabled);
        workspaces.setWorkspaces(this.workspaces.stream().map(WorkspaceExport::toWorkspace).collect(Collectors.toList()));
        return workspaces;
    }
}
