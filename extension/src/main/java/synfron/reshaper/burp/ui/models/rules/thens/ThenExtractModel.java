package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenExtract;
import synfron.reshaper.burp.core.rules.thens.entities.extract.ExtractorType;
import synfron.reshaper.burp.core.vars.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenExtractModel extends ThenModel<ThenExtractModel, ThenExtract> implements IVariableCreator {

    @Getter
    private String text = "";

    @Getter
    private ExtractorType extractorType;
    @Getter
    protected String extractor = "";
    @Getter
    private VariableSource listVariableSource;
    @Getter
    private String listVariableName = "";
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private SetListItemsPlacement itemsPlacement;

    public ThenExtractModel(ProtocolType protocolType, ThenExtract then, Boolean isNew) {
        super(protocolType, then, isNew);
        text = VariableString.toString(then.getText(), text);
        extractorType = then.getExtractorType();
        extractor = VariableString.toString(then.getExtractor(), extractor);
        listVariableSource = then.getListVariableSource();
        listVariableName = VariableString.toString(then.getListVariableName(), listVariableName);
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        itemsPlacement = then.getItemsPlacement();
        VariableCreatorRegistry.register(this);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setExtractorType(ExtractorType extractorType) {
        this.extractorType = extractorType;
        propertyChanged("extractorType", extractorType);
    }

    public void setExtractor(String extractor) {
        this.extractor = extractor;
        propertyChanged("extractor", extractor);
    }

    public void setListVariableSource(VariableSource listVariableSource) {
        this.listVariableSource = listVariableSource;
        propertyChanged("listVariableSource", listVariableSource);
    }

    public void setListVariableName(String listVariableName) {
        this.listVariableName = listVariableName;
        propertyChanged("listVariableName", listVariableName);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setItemsPlacement(SetListItemsPlacement itemsPlacement) {
        this.itemsPlacement = itemsPlacement;
        propertyChanged("itemsPlacement", itemsPlacement);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(text)) {
            errors.add("Text is required");
        }
        if (StringUtils.isEmpty(extractor)) {
            errors.add(extractorType.getSyntax() + " is required");
        } else if (extractorType == ExtractorType.Chunk && !VariableString.isPotentialInt(extractor)) {
            errors.add(extractorType.getSyntax() + "Index must be an integer");
        }
        if (StringUtils.isEmpty(listVariableName)) {
            errors.add("List Variable Name is required");
        } else if (!VariableString.isValidVariableName(listVariableName)) {
            errors.add("List Variable Name is invalid");
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setText(VariableString.getAsVariableString(text));
        ruleOperation.setExtractorType(extractorType);
        ruleOperation.setExtractor(VariableString.getAsVariableString(extractor));
        ruleOperation.setListVariableSource(listVariableSource);
        ruleOperation.setListVariableName(VariableString.getAsVariableString(listVariableName));
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setItemsPlacement(itemsPlacement);
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return VariableTag.getShortTag(listVariableSource, listVariableName);
    }

    @Override
    public RuleOperationModelType<ThenExtractModel, ThenExtract> getType() {
        return ThenModelType.Extract;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(listVariableName) ?
                List.of(new VariableSourceEntry(listVariableSource, List.of(listVariableName))) :
                Collections.emptyList();
    }
}
