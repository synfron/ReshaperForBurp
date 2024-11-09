package synfron.reshaper.burp.ui.models.workspaces;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.ui.utils.IPrompterModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class WorkspaceNameModel implements IPrompterModel<WorkspaceNameModel> {
    private final Workspace workspace;
    private String workspaceName;
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    private boolean dismissed;
    private boolean invalidated = true;

    public WorkspaceNameModel(Workspace workspace) {
        this.workspace = workspace;
        this.workspaceName = workspace.getWorkspaceName();
    }

    public WorkspaceNameModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
        propertyChanged("workspaceName", workspaceName);
    }

    public boolean save() {
        if (validate().isEmpty()) {
            workspace.setWorkspaceName(workspaceName);

            setInvalidated(false);
            return true;
        }
        setInvalidated(true);
        return false;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    @Override
    public boolean submit() {
        if (save()) {
            setDismissed(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel() {
        setDismissed(true);
        return true;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(workspaceName)) {
            errors.add("Workspace Name is required");
        } else if (!Objects.equals(getWorkspace().getWorkspaceName(), workspaceName) &&
                Workspaces.get().getWorkspaces().stream().anyMatch(workspace -> Objects.equals(workspace.getWorkspaceName(), workspaceName))
        ) {
            errors.add("Workspace Name must be unique");
        }
        return errors;
    }
}
