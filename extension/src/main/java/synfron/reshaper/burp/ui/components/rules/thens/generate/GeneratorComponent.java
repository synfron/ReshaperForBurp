package synfron.reshaper.burp.ui.components.rules.thens.generate;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IGeneratorModel;

import javax.swing.*;

public abstract class GeneratorComponent<T extends IGeneratorModel<T>> extends JPanel implements IFormComponent {

    protected final T model;
    protected final boolean allowVariableTags;

    public GeneratorComponent(T model, boolean allowVariableTags) {
        this.model = model;
        this.allowVariableTags = allowVariableTags;
        setLayout(new MigLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        initComponent();
    }

    protected abstract void initComponent();
}
