package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenPrompt;
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

    public ThenPromptModel(ProtocolType protocolType, ThenPrompt then, Boolean isNew) {
        super(protocolType, then, isNew);
        description = VariableString.toString(then.getDescription(), description);
        starterText = VariableString.toString(then.getStarterText(), starterText);
        failAfter = VariableString.toString(then.getFailAfter(), failAfter);
        breakAfterFailure = then.isBreakAfterFailure();
        captureVariableSource = then.getCaptureVariableSource();
        captureVariableName = VariableString.toString(then.getCaptureVariableName(), captureVariableName);
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
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setDescription(VariableString.getAsVariableString(description));
        ruleOperation.setStarterText(VariableString.getAsVariableString(starterText));
        ruleOperation.setFailAfter(VariableString.getAsVariableString(failAfter));
        ruleOperation.setBreakAfterFailure(breakAfterFailure);
        ruleOperation.setCaptureVariableSource(captureVariableSource);
        ruleOperation.setCaptureVariableName(VariableString.getAsVariableString(captureVariableName));
        setValidated(true);
        return true;
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    public RuleOperationModelType<ThenPromptModel, ThenPrompt> getType() {
        return ThenModelType.Prompt;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(captureVariableName) ?
                List.of(new VariableSourceEntry(captureVariableSource, captureVariableName)) :
                Collections.emptyList();
    }
}
