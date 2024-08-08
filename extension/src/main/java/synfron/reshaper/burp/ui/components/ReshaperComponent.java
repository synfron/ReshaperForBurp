/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synfron.reshaper.burp.ui.components;

import lombok.Getter;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.ui.components.workspaces.WorkspaceComponent;
import synfron.reshaper.burp.ui.components.workspaces.WorkspaceNameOptionPane;
import synfron.reshaper.burp.ui.models.workspaces.WorkspaceNameModel;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ReshaperComponent extends JPanel {

    @Getter
    private final Workspaces workspaces;
    private JTabbedPane tabs;
    private final IEventListener<PropertyChangedArgs> workspacesPropertyChangedListener = this::onWorkspacesPropertyChanged;
    private final IEventListener<CollectionChangedArgs> workspacesChangedListener = this::onWorkspacesChangedListener;

    public ReshaperComponent(Workspaces workspaces) {
        this.workspaces = workspaces;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBody();

        workspaces.withListener(workspacesPropertyChangedListener)
                .withCollectionListener(workspacesChangedListener);
    }

    private void onWorkspacesChangedListener(CollectionChangedArgs collectionChangedArgs) {
        switch (collectionChangedArgs.getAction()) {
            case Add -> {
                addTab(new WorkspaceComponent((Workspace) collectionChangedArgs.getItem()));
                tabs.setSelectedComponent(tabs.getComponentAt(tabs.getTabCount() - 1));
            }
            case Remove -> {
                for (int tabIndex = 0; tabIndex < tabs.getTabCount(); tabIndex++) {
                    WorkspaceComponent workspaceComponent = (WorkspaceComponent) tabs.getComponentAt(tabIndex);
                    if (workspaceComponent.getWorkspace() == collectionChangedArgs.getItem()) {
                        tabs.remove(workspaceComponent);
                    }
                }
            }
        }
    }

    private void onWorkspacesPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("enabled")) {
            setBody();
        } else if (propertyChangedArgs.getName().equals("workspaces")) {
            addOrRemoveTabs(null);
        }
    }

    private void setBody() {
        WorkspaceComponent workspaceComponent = null;
        if (getComponentCount() > 0) {
            workspaceComponent = getCurrentWorkspaceComponent();
            removeAll();
        }
        if (workspaceComponent == null) {
            add(workspaces.isEnabled() ? getTabs(null) : new WorkspaceComponent(workspaces.getDefault()));
        } else {
            add(workspaces.isEnabled() ? getTabs(workspaceComponent) : workspaceComponent);
        }
    }

    private WorkspaceComponent getCurrentWorkspaceComponent() {
        Component currentComponent = getComponent(0);
        if (currentComponent instanceof WorkspaceComponent workspaceComponent) {
            return workspaceComponent;
        } else if (currentComponent instanceof JTabbedPane tabbedPane) {
            return (WorkspaceComponent)tabbedPane.getComponentAt(0);
        }
        return null;
    }

    private JTabbedPane getTabs(WorkspaceComponent currentWorkspaceComponent) {
        tabs = new JTabbedPane();
        addOrRemoveTabs(currentWorkspaceComponent);
        return tabs;
    }
    private void addOrRemoveTabs(WorkspaceComponent currentWorkspaceComponent) {
        HashMap<UUID, WorkspaceComponent> existingTabs = new HashMap<>();
        for (int tabIndex = 0; tabIndex < tabs.getTabCount(); tabIndex++) {
            WorkspaceComponent workspaceComponent = (WorkspaceComponent) tabs.getComponentAt(tabIndex);
            Workspace workspace = workspaces.get(workspaceComponent.getWorkspace().getWorkspaceUuid());
            if (workspace != null) {
                existingTabs.put(workspace.getWorkspaceUuid(), workspaceComponent);
            }
        }
        if (currentWorkspaceComponent != null) {
            existingTabs.put(currentWorkspaceComponent.getWorkspace().getWorkspaceUuid(), currentWorkspaceComponent);
        }
        Component activeTab = tabs.getSelectedComponent();
        tabs.removeAll();

        for (Workspace workspace : workspaces.getWorkspaces()) {
            WorkspaceComponent component = existingTabs.get(workspace.getWorkspaceUuid());
            if (component == null) {
                component = new WorkspaceComponent(workspace);
            }
            addTab(component);
            if (
                    (activeTab == null && Objects.equals(workspace.getWorkspaceUuid(), workspaces.getDefaultWorkspaceUuid())) ||
                    activeTab == component
            ) {
                tabs.setSelectedComponent(component);
            }
        }
    }

    private void addTab(WorkspaceComponent component) {
        tabs.addTab(component.getWorkspace().getWorkspaceName(), component);
        JLabel label = new WorkspaceTabLabel(component).getComponent();
        tabs.setTabComponentAt(tabs.getTabCount() - 1, label);
    }

    private class WorkspaceTabLabel {
        private final WorkspaceComponent workspaceComponent;
        private final Workspace workspace;
        private JLabel label;
        private final IEventListener<PropertyChangedArgs> workspacePropertyChangedListener = this::onWorkspacePropertyChanged;

        public WorkspaceTabLabel(WorkspaceComponent workspaceComponent) {
            this.workspaceComponent = workspaceComponent;
            this.workspace = workspaceComponent.getWorkspace();
        }

        private void onWorkspacePropertyChanged(PropertyChangedArgs propertyChangedArgs) {
            if (propertyChangedArgs.getName().equals("workspaceName")) {
                label.setText(workspace.getWorkspaceName());
            }
        }

        public JLabel getComponent() {
            if (label == null) {
                label = new JLabel(workspace.getWorkspaceName());

                JPopupMenu contextMenu = new JPopupMenu();
                JMenuItem rename = new JMenuItem("Rename");
                JMenuItem delete = new JMenuItem("Delete");
                JMenuItem defaultWorkspace = new JMenuItem("Set As Default");
                JMenuItem addNewWorkspace = new JMenuItem("Add New Workspace");
                JMenuItem disableWorkspaces = new JMenuItem("Disable Workspaces");

                rename.addActionListener(this::onRename);
                delete.addActionListener(this::onDelete);
                defaultWorkspace.addActionListener(this::onDefaultWorkspace);
                addNewWorkspace.addActionListener(this::onAddNewWorkspace);
                disableWorkspaces.addActionListener(this::onDisableWorkspaces);

                contextMenu.add(rename);
                contextMenu.add(delete);
                contextMenu.add(defaultWorkspace);
                contextMenu.add(addNewWorkspace);
                contextMenu.add(disableWorkspaces);

                label.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if ((SwingUtilities.isLeftMouseButton(e))) {
                            tabs.setSelectedComponent(workspaceComponent);
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });

                workspace.withListener(workspacePropertyChangedListener);

                contextMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        delete.setVisible(workspaces.getWorkspaces().size() > 1);
                        defaultWorkspace.setVisible(!Objects.equals(workspace.getWorkspaceUuid(), workspaces.getDefaultWorkspaceUuid()));
                        disableWorkspaces.setVisible(workspaces.getWorkspaces().size() == 1);
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });

                label.setComponentPopupMenu(contextMenu);
            }
            return label;
        }

        private void onDisableWorkspaces(ActionEvent actionEvent) {
            workspaces.setEnabled(false);
        }

        private void onAddNewWorkspace(ActionEvent actionEvent) {
            Workspace workspace = new Workspace(UUID.randomUUID(), "");
            WorkspaceNameModel model = new WorkspaceNameModel(workspace, ignored -> Workspaces.get().add(workspace));
            ModalPrompter.open(model, ignored -> WorkspaceNameOptionPane.showDialog(model), true);
        }

        private void onDefaultWorkspace(ActionEvent actionEvent) {
            workspaces.setDefaultWorkspaceUuid(workspace.getWorkspaceUuid());
        }

        private void onRename(ActionEvent actionEvent) {
            WorkspaceNameModel model = new WorkspaceNameModel(workspace);
            ModalPrompter.open(model, ignored -> WorkspaceNameOptionPane.showDialog(model), true);
        }

        private void onDelete(ActionEvent actionEvent) {

            int response = JOptionPane.showConfirmDialog(ReshaperComponent.this, String.format("Are you sure you want to delete workspace '%s'? All rules, variables, and settings in the workspace will be deleted.", workspace.getWorkspaceName()), "Delete Workspace", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                workspaces.delete(workspace);
            }
        }
    }
}
