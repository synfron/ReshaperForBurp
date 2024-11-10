package synfron.reshaper.burp.ui.components.rules.thens.transform;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.transform.TransformerModel;
import synfron.reshaper.burp.ui.utils.DocumentActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class TransformerComponent<T extends TransformerModel<T, ?>> extends JPanel implements IFormComponent {

    protected final T model;
    private JTextField input;

    public TransformerComponent(T model) {
        this.model = model;

        setLayout(new MigLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        input = createTextField(true);

        input.setText(model.getInput());

        input.getDocument().addDocumentListener(new DocumentActionListener(this::onInputChanged));

        add(getLabeledField("Input *", input), "wrap");
        
        initComponent();
    }

    protected abstract void initComponent();

    private void onInputChanged(ActionEvent actionEvent) {
        model.setInput(input.getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
