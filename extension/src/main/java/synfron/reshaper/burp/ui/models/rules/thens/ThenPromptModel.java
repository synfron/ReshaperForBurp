package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenPrompt;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenPromptModel extends ThenModel<ThenPromptModel, ThenPrompt> implements IVariableCreator {

    @Getter
    private String description;
    @Getter
    private String starterText;
    @Getter
    private String failAfter = "30000";
    @Getter
    private boolean breakAfterFailure;
    @Getter
    private VariableSource captureVariableSource;
    @Getter
    private String captureVariableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;

    public ThenPromptModel(ProtocolType protocolType, ThenPrompt then, Boolean isNew) {
        super(protocolType, then, isNew);
        description = VariableString.toString(then.getDescription(), description);
        starterText = VariableString.toString(then.getStarterText(), starterText);
        failAfter = VariableString.toString(then.getFailAfter(), failAfter);
        breakAfterFailure = then.isBreakAfterFailure();
        captureVariableSource = then.getCaptureVariableSource();
        captureVariableName = VariableString.toString(then.getCaptureVariableName(), captureVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    public void setDescription(String description) {
        this.description = description;
        propertyChanged("description", description);
    }

    public void setStarterText(String starterText) {
        this.starterText = starterText;
        propertyChanged("starterText", starterText);
    }

    public void setFailAfter(String failAfter) {
        this.failAfter = failAfter;
        propertyChanged("failAfter", failAfter);
    }

    public void setBreakAfterFailure(boolean breakAfterFailure) {
        this.breakAfterFailure = breakAfterFailure;
        propertyChanged("breakAfterFailure", breakAfterFailure);
    }

    public void setCaptureVariableSource(VariableSource captureVariableSource) {
        this.captureVariableSource = captureVariableSource;
        propertyChanged("captureVariableSource", captureVariableSource);
    }

    public void setCaptureVariableName(String captureVariableName) {
        this.captureVariableName = captureVariableName;
        propertyChanged("captureVariableName", captureVariableName);
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
        if (StringUtils.isEmpty(description)) {
            errors.add("Description is required");
        }
        if (StringUtils.isEmpty(failAfter)) {
            errors.add("Fail After is required");
        } else if (!VariableString.isPotentialInt(failAfter)) {
            errors.add("Fail After must be an integer");
        }
        if (StringUtils.isEmpty(captureVariableName)) {
            errors.add("Capture Variable Name is required");
        } else if (!VariableString.isValidVariableName(captureVariableName)) {
            errors.add("Capture Variable Name is invalid");
        }
        if (captureVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
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
        ruleOperation.setDescription(VariableString.getAsVariableString(description));
        ruleOperation.setStarterText(VariableString.getAsVariableString(starterText));
        ruleOperation.setFailAfter(VariableString.getAsVariableString(failAfter));
        ruleOperation.setBreakAfterFailure(breakAfterFailure);
        ruleOperation.setCaptureVariableSource(captureVariableSource);
        ruleOperation.setCaptureVariableName(VariableString.getAsVariableString(captureVariableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(description);
    }

    @Override
    public RuleOperationModelType<ThenPromptModel, ThenPrompt> getType() {
        return ThenModelType.Prompt;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(captureVariableName) ?
                List.of(new VariableSourceEntry(captureVariableSource, List.of(captureVariableName))) :
                Collections.emptyList();
    }
}
