package synfron.reshaper.burp.core.settings;

import lombok.Data;
import lombok.Getter;
import synfron.reshaper.burp.core.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Workspaces {
    private UUID defaultWorkspaceUuid = null;

    private boolean enabled;
    private List<Workspace> workspaces = new ArrayList<>();
    private ThreadLocal<Workspace> currentWorkspace = new ThreadLocal<>();
    private static Workspaces instance;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter
    private final CollectionChangedEvent collectionChangedEvent = new CollectionChangedEvent();

    public static Workspaces get() {
        if (instance == null) {
            instance = SettingsManager.loadPersistentWorkspaces();
        }
        return instance;
    }

    public void initialize() {
        if (workspaces.isEmpty()) {
            workspaces.add(new Workspace(UUID.randomUUID(), "Default Workspace"));
        }
        if (defaultWorkspaceUuid == null) {
            defaultWorkspaceUuid = workspaces.getFirst().getWorkspaceUuid();
        }
    }

    public void add(Workspace workspace) {
        workspaces.add(workspace);
        collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Add, workspace.getWorkspaceUuid(), workspace));
    }

    public void delete(Workspace workspace) {
        if (workspaces.size() > 1) {
            if (workspace.getWorkspaceUuid().equals(defaultWorkspaceUuid)) {
                defaultWorkspaceUuid = workspaces.stream()
                        .map(Workspace::getWorkspaceUuid)
                        .filter(workspaceUuid -> !workspaceUuid.equals(workspace.getWorkspaceUuid()))
                        .findFirst()
                        .get();
            }
            workspace.unload();
            workspaces.remove(workspace);
            collectionChangedEvent.invoke(new CollectionChangedArgs(this, CollectionChangedAction.Remove, workspace.getWorkspaceUuid(), workspace));
        }
    }

    public Workspace getDefault() {
        return workspaces.stream()
                .filter(workspace -> workspace.getWorkspaceUuid().equals(defaultWorkspaceUuid))
                .findFirst().orElse(workspaces.getFirst());
    }

    public Workspace get(UUID workspaceUuid) {
        return workspaces.stream().filter(workspace -> workspace.getWorkspaceUuid().equals(workspaceUuid)).findFirst().orElse(null);
    }

    public void setCurrentWorkspace(Workspace workspace) {
        currentWorkspace.set(workspace);
    }

    public Workspace getCurrentWorkspace() {
        return currentWorkspace.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        propertyChanged("enabled", enabled);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public Workspaces withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public Workspaces withCollectionListener(IEventListener<CollectionChangedArgs> listener) {
        collectionChangedEvent.add(listener);
        return this;
    }

    public void unload() {
        SettingsManager.savePersistentWorkspaces(new WorkspacesExport(get()));
        get().getWorkspaces().forEach(Workspace::unload);
        instance = null;
    }
}
