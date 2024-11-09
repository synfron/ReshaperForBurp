package synfron.reshaper.burp.ui.components.shared;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.ui.utils.IPrompterModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class FormPrompt<T extends IPrompterModel<T>> extends JDialog {

    private final Component formComponent;
    private final T model;
    private final IEventListener<PropertyChangedArgs> modelChangedListener = this::onModelChanged;
    @Getter @Setter
    private boolean setAutoResize;
    private Map<String, Object> clientProperties;
    private boolean autoResize = true;

    public FormPrompt(String title, T model, Component formComponent) {
        super(null, title, Dialog.ModalityType.APPLICATION_MODAL);
        this.formComponent = formComponent;
        this.model = model;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(false);
        setLayout(new BorderLayout());
        
        initComponent();

        model.withListener(modelChangedListener);
    }

    private void initComponent() {
        JPanel actionBar = getActionBar();

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(5,5,5,5));
        container.add(formComponent, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
        add(actionBar, BorderLayout.PAGE_END);
    }

    private void onModelChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("dismissed")) {
            setVisible(!model.isDismissed());
        } else if (propertyChangedArgs.getName().equals("fieldsSize") && autoResize) {
            pack();
        }

    }

    private JPanel getActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton submit = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        submit.addActionListener(this::onSubmit);
        cancel.addActionListener(this::onCancel);

        actionBar.add(submit);
        actionBar.add(cancel);

        return actionBar;
    }

    private void onCancel(ActionEvent actionEvent) {
        model.cancel();
    }

    private void onSubmit(ActionEvent actionEvent) {
        model.submit();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && autoResize) {
            pack();
        }
    }

    @Override
    public void setSize(Dimension size) {
        autoResize = false;
        super.setSize(size);
    }

    public Object getClientProperty(String key) {
        return clientProperties != null ? clientProperties.get(key) : null;
    }

    public void putClientProperty(String key, Object value) {
        if (clientProperties == null) {
            clientProperties = new HashMap<>();
        }
        clientProperties.put(key, value);
    }
}
