package synfron.reshaper.burp.ui.components.rules.thens.generate;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.models.rules.thens.generate.GeneratorModel;

import javax.swing.*;

public abstract class GeneratorComponent<T extends GeneratorModel<T, ?>> extends JPanel implements IFormComponent {

    protected final T model;

    public GeneratorComponent(T model) {
        this.model = model;

        setLayout(new MigLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        initComponent();
    }

    protected abstract void initComponent();
}
