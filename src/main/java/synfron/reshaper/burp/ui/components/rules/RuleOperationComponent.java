package synfron.reshaper.burp.ui.components.rules;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.IRuleOperation;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

}
