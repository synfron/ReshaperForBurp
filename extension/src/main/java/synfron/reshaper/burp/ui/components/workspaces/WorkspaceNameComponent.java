package synfron.reshaper.burp.ui.components.workspaces;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.workspaces.WorkspaceNameModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WorkspaceNameComponent extends JPanel implements IFormComponent {

    private final WorkspaceNameModel model;
    private JTextField workspaceName;
    private final IEventListener<PropertyChangedArgs> modelChangedListener = this::onModelChanged;

    public WorkspaceNameComponent(WorkspaceNameModel model) {
        this.model = model;
        initComponent();

        model.withListener(modelChangedListener);
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("invalidated") && model.isInvalidated()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponent() {
        add(getBody(), BorderLayout.CENTER);
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        workspaceName = createTextField(false);

        workspaceName.setText(model.getWorkspaceName());

        workspaceName.getDocument().addDocumentListener(new DocumentActionListener(this::onWorkspaceNameChanged));

        container.add(getLabeledField("Workspace Name *", workspaceName), "wrap");;

        return container;
    }

    private void onWorkspaceNameChanged(ActionEvent actionEvent) {
        model.setWorkspaceName(workspaceName.getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
