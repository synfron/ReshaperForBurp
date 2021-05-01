package synfron.reshaper.burp.ui.components.rules;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class RuleOperationComponent<P extends RuleOperationModel<P, T>, T extends IRuleOperation<T>> extends JScrollPane implements IFormComponent {

    @Getter
    protected final P model;
    protected final JPanel mainContainer;
    protected final JButton validate;
    private final IEventListener<PropertyChangedArgs> modelPropertyChangedListener = this::onModelPropertyChanged;

    protected RuleOperationComponent(P model) {
        this.model = model;
        mainContainer = new JPanel(new MigLayout());
        setViewportView(mainContainer);

        validate = new JButton("Validate");
        setValidateButtonState();
        validate.addActionListener(this::onValidate);

        model.getPropertyChangedEvent().add(modelPropertyChangedListener);
    }

    private void onModelPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if ("validated".equals(propertyChangedArgs.getName())) {
            setValidateButtonState();
        }
    }

    protected Component getPaddedButton(JButton button) {
        JPanel outerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerContainer.setAlignmentX(LEFT_ALIGNMENT);
        outerContainer.setAlignmentY(TOP_ALIGNMENT);
        outerContainer.add(button);
        return outerContainer;
    }

    private void setValidateButtonState() {
        if (model.isValidated()) {
            validate.setEnabled(false);
            validate.setText("Validated");
        } else {
            validate.setEnabled(true);
            validate.setText("Validate");
        }
    }

    private void onValidate(ActionEvent actionEvent) {
        if (!model.persist()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", model.validate()),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected static Component withVisibilityFieldChangeDependency(
            Component dependent,
            Component dependee,
            BooleanSupplier condition
    ) {
        return withVisibilityFieldChangeDependency(dependent, List.of(dependee), condition);
    }

    protected static Component withVisibilityFieldChangeDependency(
            Component dependent,
            List<Component> dependees,
            BooleanSupplier condition
    ) {
        JPanel container = new JPanel();
        ((FlowLayout)container.getLayout()).setHgap(0);
        ((FlowLayout)container.getLayout()).setVgap(0);
        if (dependent.isEnabled()) {
            container.add(dependent);
        }
        dependees.forEach(dependee -> {
            if (dependee instanceof JTextField) {
                ((JTextField) dependee).getDocument().addDocumentListener(new DocumentActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition)));
            } else if (dependee instanceof JCheckBox) {
                ((JCheckBox) dependee).addActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition));
            } else if (dependee instanceof JComboBox) {
                ((JComboBox<?>) dependee).addActionListener(event -> updateVisibilityFieldChangeDependency(container, dependent, condition));
            }
        });
        updateVisibilityFieldChangeDependency(container, dependent, condition);
        return container;
    }

    private static void updateVisibilityFieldChangeDependency(JPanel container, Component dependent, BooleanSupplier condition) {
        boolean isVisible = condition.getAsBoolean();
        if (isVisible != dependent.isEnabled()) {
            if (isVisible) {
                container.add(dependent);
            } else {
                container.remove(dependent);
            }
            container.revalidate();
            container.repaint();
            dependent.setEnabled(isVisible);
        }
    }
}
