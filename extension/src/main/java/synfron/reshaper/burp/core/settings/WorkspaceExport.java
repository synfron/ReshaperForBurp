package synfron.reshaper.burp.core.settings;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkspaceExport extends WorkspaceDataExport {
    private UUID workspaceUuid;
    private String workspaceName;
    private GeneralSettings generalSettings;

    public WorkspaceExport(Workspace workspace) {
        super(workspace);
        workspaceUuid = workspace.getWorkspaceUuid();
        workspaceName = workspace.getWorkspaceName();
        generalSettings = workspace.getGeneralSettings();
    }

    public void copyTo(Workspace workspace, boolean overwriteDuplicates) {
        workspace.getGeneralSettings().importSettings(generalSettings);
        super.copyTo(workspace, overwriteDuplicates);
    }

    public Workspace toWorkspace() {
        Workspace workspace = new Workspace(workspaceUuid, workspaceName);
        copyTo(workspace, false);
        return workspace;
    }
}
