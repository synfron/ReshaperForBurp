package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.rules.thens.ThenGenerate;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IGenerator;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.generate.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ThenGenerateModel extends ThenModel<ThenGenerateModel, ThenGenerate> implements IVariableCreator {

    private GenerateOption generateOption;

    private GeneratorModel<?, ?> generator;
    private final Map<GenerateOption, GeneratorModel<?,?>> generatorMap = new HashMap<>();
    private VariableSource destinationVariableSource;
    private String destinationVariableName;
    private SetListItemPlacement itemPlacement;
    private String delimiter = "{{s:n}}";
    private String index;
    private final IEventListener<PropertyChangedArgs> generatorPropertyChangedListener = this::onGeneratorPropertyChanged;

    public ThenGenerateModel(ProtocolType protocolType, ThenGenerate then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.generateOption = then.getGenerateOption();
        this.generator = generatorMap.computeIfAbsent(this.generateOption, this::constructGeneratorModel);
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.destinationVariableName = VariableString.toString(then.getDestinationVariableName(), destinationVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    private GeneratorModel<?,?> constructGeneratorModel(GenerateOption generateOption) {
        return (switch (generateOption) {
            case Uuid -> new UuidGeneratorModel(constructGenerator(generateOption));
            case Words -> new WordGeneratorModel(constructGenerator(generateOption));
            case Bytes -> new BytesGeneratorModel(constructGenerator(generateOption));
            case Integer -> new IntegerGeneratorModel(constructGenerator(generateOption));
            case IpAddress -> new IpAddressGeneratorModel(constructGenerator(generateOption));
            case Timestamp -> new TimestampGeneratorModel(constructGenerator(generateOption));
            case UnixTimestamp -> new UnixTimestampGeneratorModel(constructGenerator(generateOption));
            case Password -> new PasswordGeneratorModel(constructGenerator(generateOption));
        }).withListener(generatorPropertyChangedListener);
    }

    private void onGeneratorPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        setValidated(false);
    }

    private <T extends IGenerator> T constructGenerator(GenerateOption generateOption) {
        return generateOption.getGeneratorClass().isInstance(ruleOperation.getGenerator()) ?
                (T)ruleOperation.getGenerator() :
                (T) ObjectUtils.construct(generateOption.getGeneratorClass());
    }

    public void setGenerateOption(GenerateOption generateOption) {
        this.generateOption = generateOption;
        propertyChanged("generateOption", generateOption);
        setGenerator(generatorMap.computeIfAbsent(this.generateOption, this::constructGeneratorModel));
    }

    public void setGenerator(GeneratorModel<?, ?> generator) {
        this.generator = generator;
        propertyChanged("generator", generator);
    }

    public void setDestinationVariableSource(VariableSource destinationVariableSource) {
        this.destinationVariableSource = destinationVariableSource;
        propertyChanged("destinationVariableSource", destinationVariableSource);
    }

    public void setDestinationVariableName(String destinationVariableName) {
        this.destinationVariableName = destinationVariableName;
        propertyChanged("destinationVariableName", destinationVariableName);
    }

    public void setItemPlacement(SetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        errors.addAll(generator.validate());
        if (StringUtils.isEmpty(destinationVariableName)) {
            errors.add("Destination Variable Name is required");
        } else if (!VariableString.isValidVariableName(destinationVariableName)) {
            errors.add("Destination Variable Name is invalid");
        }
        if (destinationVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setGenerateOption(generateOption);
        generator.persist();
        ruleOperation.setGenerator(generator.getGenerator());
        ruleOperation.setDestinationVariableSource(destinationVariableSource);
        ruleOperation.setDestinationVariableName(VariableString.getAsVariableString(destinationVariableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return generateOption.name();
    }

    @Override
    public RuleOperationModelType<ThenGenerateModel, ThenGenerate> getType() {
        return ThenModelType.Generate;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(destinationVariableName) ?
                List.of(new VariableSourceEntry(destinationVariableSource, List.of(destinationVariableName))) :
                Collections.emptyList();
    }
}
