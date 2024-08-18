package synfron.reshaper.burp.ui.components.workspaces;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.workspaces.WorkspaceNameModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;

public class WorkspaceNameOptionPane extends JOptionPane implements IFormComponent {

    private final JPanel container;
    private final WorkspaceNameModel model;
    private JTextField workspaceName;

    private WorkspaceNameOptionPane(WorkspaceNameModel model) {
        super(new JPanel(new BorderLayout()), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[]{ "OK", "Cancel" }, "OK");
        container = (JPanel)message;
        this.model = model;
        addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, this::onPropertyChanged);
        initComponent();
    }

    private void onPropertyChanged(PropertyChangeEvent event) {
        if (Objects.equals(getValue(), "OK")) {
            if (!model.save()) {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", model.validate()),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            model.setDismissed(true);
        }
    }

    public static void showDialog(WorkspaceNameModel model) {
        WorkspaceNameOptionPane optionPane = new WorkspaceNameOptionPane(model);
        JDialog dialog = optionPane.createDialog("Workspace");
        dialog.setResizable(true);

        dialog.setModal(false);
        dialog.setVisible(true);
    }

    private void initComponent() {
        container.add(getBody(), BorderLayout.CENTER);
    }

    private Component getBody() {
        JPanel container = new JPanel(new MigLayout());

        workspaceName = createTextField(false);

        workspaceName.setText(model.getWorkspaceName());

        workspaceName.getDocument().addDocumentListener(new DocumentActionListener(this::onWorkspaceNameChanged));

        container.add(getLabeledField("Workspace Name", workspaceName), "wrap");;

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
