package synfron.reshaper.burp.ui.components.rules.wizard.vars;

import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.components.rules.thens.generate.*;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.GeneratorVariableTagWizardModel;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.generator.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GeneratorVariableTagWizardComponent extends JPanel implements IFormComponent {
    private final GeneratorVariableTagWizardModel model;
    private final JPanel generatorContainer = new JPanel();
    private JComboBox<GenerateOption> generateOption;

    public GeneratorVariableTagWizardComponent(GeneratorVariableTagWizardModel model) {
        this.model = model;
        initComponent();
    }

    private GeneratorComponent<?> getGenerator() {
        return switch (model.getGenerateOption()) {
            case Uuid -> new UuidGeneratorComponent((UuidGeneratorVariableModel) model.getGenerator(), false);
            case Words -> new WordGeneratorComponent((WordGeneratorVariableModel) model.getGenerator(), false);
            case Bytes -> new BytesGeneratorComponent((BytesGeneratorVariableModel) model.getGenerator(), false);
            case Integer -> new IntegerGeneratorComponent((IntegerGeneratorVariableModel) model.getGenerator(), false);
            case IpAddress -> new IpAddressGeneratorComponent((IpAddressGeneratorVariableModel) model.getGenerator(), false);
            case Timestamp -> new TimestampGeneratorComponent((TimestampGeneratorVariableModel) model.getGenerator(), false);
            case UnixTimestamp -> new UnixTimestampGeneratorComponent((UnixTimestampGeneratorVariableModel) model.getGenerator(), false);
            case Password -> new PasswordGeneratorComponent((PasswordGeneratorVariableModel) model.getGenerator(), false);
        };
    }

    private void initComponent() {
        setLayout(new MigLayout());
        generatorContainer.setBorder(BorderFactory.createEmptyBorder(0, -12, 0, 0));
        generateOption = createComboBox(GenerateOption.values());

        generateOption.setSelectedItem(model.getGenerateOption());

        generateOption.addActionListener(this::onSetGenerateOptionChanged);

        add(getLabeledField("Generate Option", generateOption), "wrap");
        add(generatorContainer, "wrap");

        setGenerator();
    }

    private void setGenerator() {
        GeneratorComponent<?> generator = getGenerator();

        generatorContainer.removeAll();
        generatorContainer.add(generator);
        revalidate();
        repaint();
    }

    private void onSetGenerateOptionChanged(ActionEvent actionEvent) {
        model.setGenerateOption((GenerateOption) generateOption.getSelectedItem());
        setGenerator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}
