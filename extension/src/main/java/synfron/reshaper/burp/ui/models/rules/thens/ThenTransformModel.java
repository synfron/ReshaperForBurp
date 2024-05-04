package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenTransform;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TransformOption;
import synfron.reshaper.burp.core.rules.thens.entities.transform.ITransformer;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.transform.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ThenTransformModel extends ThenModel<ThenTransformModel, ThenTransform> implements IVariableCreator {

    private TransformOption transformOption;

    private TransformerModel<?, ?> transformer;
    private final Map<TransformOption, TransformerModel<?,?>> transformerMap = new HashMap<>();
    private VariableSource destinationVariableSource;
    private String destinationVariableName;
    private SetListItemPlacement itemPlacement;
    private String delimiter = "{{s:n}}";
    private String index;

    public ThenTransformModel(ProtocolType protocolType, ThenTransform then, Boolean isNew) {
        super(protocolType, then, isNew);
        this.transformOption = then.getTransformOption();
        this.transformer = transformerMap.computeIfAbsent(this.transformOption, this::constructTransformerModel);
        this.destinationVariableSource = then.getDestinationVariableSource();
        this.destinationVariableName = VariableString.toString(then.getDestinationVariableName(), destinationVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    private TransformerModel<?,?> constructTransformerModel(TransformOption transformOption) {
        return switch (transformOption) {
            case Base64 -> new Base64TransformerModel(constructTransformer(transformOption));
            case TextEncode -> new TextEncodeTransformerModel(constructTransformer(transformOption));
            case JwtDecode -> new JwtDecodeTransformerModel(constructTransformer(transformOption));
            case Case -> new CaseTransformerModel(constructTransformer(transformOption));
            case Hash -> new HashTransformerModel(constructTransformer(transformOption));
            case Hex -> new HexTransformerModel(constructTransformer(transformOption));
            case Integer -> new IntegerTransformerModel(constructTransformer(transformOption));
            case Trim -> new TrimTransformerModel(constructTransformer(transformOption));
        };
    }

    private <T extends ITransformer> T constructTransformer(TransformOption transformOption) {
        return transformOption.getTransformerClass().isInstance(ruleOperation.getTransformer()) ?
                (T)ruleOperation.getTransformer() :
                (T) ObjectUtils.construct(transformOption.getTransformerClass());
    }

    public void setTransformOption(TransformOption transformOption) {
        this.transformOption = transformOption;
        propertyChanged("transformOption", transformOption);
        setTransformer(transformerMap.computeIfAbsent(this.transformOption, this::constructTransformerModel));
    }

    public void setTransformer(TransformerModel<?, ?> transformer) {
        this.transformer = transformer;
        propertyChanged("transformer", transformer);
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
        errors.addAll(transformer.validate());
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
        ruleOperation.setTransformOption(transformOption);
        transformer.persist();
        ruleOperation.setTransformer(transformer.getTransformer());
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
        return transformOption.name();
    }

    @Override
    public RuleOperationModelType<ThenTransformModel, ThenTransform> getType() {
        return ThenModelType.Transform;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(destinationVariableName) ?
                List.of(new VariableSourceEntry(destinationVariableSource, destinationVariableName)) :
                Collections.emptyList();
    }
}
