package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.ui.models.rules.wizard.vars.generator.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GeneratorVariableTagWizardModel implements IVariableTagWizardModel {

    private GenerateOption generateOption = GenerateOption.Uuid;
    private GeneratorVariableModel<?> generator;
    private final Map<GenerateOption, GeneratorVariableModel<?>> generatorMap = new HashMap<>();

    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public GeneratorVariableTagWizardModel() {
        setGenerateOption(generateOption);
    }

    private GeneratorVariableModel<?> constructGeneratorModel(GenerateOption generateOption) {
        return switch (generateOption) {
            case Uuid -> new UuidGeneratorVariableModel();
            case Words -> new WordGeneratorVariableModel();
            case Bytes -> new BytesGeneratorVariableModel();
            case Integer -> new IntegerGeneratorVariableModel();
            case IpAddress -> new IpAddressGeneratorVariableModel();
            case Timestamp -> new TimestampGeneratorVariableModel();
            case UnixTimestamp -> new UnixTimestampGeneratorVariableModel();
            case Password -> new PasswordGeneratorVariableModel();
        };
    }

    public void setGenerateOption(GenerateOption generateOption) {
        this.generateOption = generateOption;
        propertyChanged("generateOption", generateOption);
        setGenerator(generatorMap.computeIfAbsent(this.generateOption, this::constructGeneratorModel));
        propertyChanged("fieldsSize", true);
    }

    public void setGenerator(GeneratorVariableModel<?> generator) {
        this.generator = generator;
        propertyChanged("generator", generator);
        SwingUtilities.invokeLater(() -> propertyChanged("fieldsSize", true));
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public GeneratorVariableTagWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        getPropertyChangedEvent().add(listener);
        return this;
    }

    @Override
    public List<String> validate() {
        return generator.validate();
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.Generator;
    }

    @Override
    public String getTag() {
        return generator.getTag();
    }
}
