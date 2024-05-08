package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRunProcess;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenRunProcessModel extends ThenModel<ThenRunProcessModel, ThenRunProcess> implements IVariableCreator {

    @Getter
    private String command;
    @Getter
    private String input;
    @Getter
    private boolean waitForCompletion;
    @Getter
    private String failAfter = "5000";
    @Getter
    private boolean killAfterFailure;
    @Getter
    private boolean failOnNonZeroExitCode;
    @Getter
    private boolean breakAfterFailure;
    @Getter
    private boolean captureOutput;
    @Getter
    private boolean captureAfterFailure;
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

    public ThenRunProcessModel(ProtocolType protocolType, ThenRunProcess then, Boolean isNew) {
        super(protocolType, then, isNew);
        command = VariableString.toString(then.getCommand(), command);
        input = VariableString.toString(then.getInput(), input);
        waitForCompletion = then.isWaitForCompletion();
        failAfter = VariableString.toString(then.getFailAfter(), failAfter);
        killAfterFailure = then.isKillAfterFailure();
        failOnNonZeroExitCode = then.isFailOnNonZeroExitCode();
        breakAfterFailure = then.isBreakAfterFailure();
        captureOutput = then.isCaptureOutput();
        captureAfterFailure = then.isCaptureAfterFailure();
        captureVariableSource = then.getCaptureVariableSource();
        captureVariableName = VariableString.toString(then.getCaptureVariableName(), captureVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    public void setCommand(String command) {
        this.command = command;
        propertyChanged("command", command);
    }

    public void setInput(String input) {
        this.input = input;
        propertyChanged("input", input);
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
        propertyChanged("waitForCompletion", waitForCompletion);
    }

    public void setFailAfter(String failAfter) {
        this.failAfter = failAfter;
        propertyChanged("failAfter", failAfter);
    }

    public void setKillAfterFailure(boolean killAfterFailure) {
        this.killAfterFailure = killAfterFailure;
        propertyChanged("killAfterFailure", killAfterFailure);
    }

    public void setFailOnNonZeroExitCode(boolean failOnNonZeroExitCode) {
        this.failOnNonZeroExitCode = failOnNonZeroExitCode;
        propertyChanged("failOnNonZeroExitCode", failOnNonZeroExitCode);
    }

    public void setBreakAfterFailure(boolean breakAfterFailure) {
        this.breakAfterFailure = breakAfterFailure;
        propertyChanged("breakAfterFailure", breakAfterFailure);
    }

    public void setCaptureOutput(boolean captureOutput) {
        this.captureOutput = captureOutput;
        propertyChanged("captureOutput", captureOutput);
    }

    public void setCaptureAfterFailure(boolean captureAfterFailure) {
        this.captureAfterFailure = captureAfterFailure;
        propertyChanged("captureAfterFailure", captureAfterFailure);
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
        if (StringUtils.isEmpty(command)) {
            errors.add("Command is required");
        }
        if (waitForCompletion && StringUtils.isEmpty(failAfter)) {
            errors.add("Fail After is required");
        } else if (waitForCompletion && !VariableString.isPotentialInt(failAfter)) {
            errors.add("Fail After must be an integer");
        }
        if (waitForCompletion && captureOutput && StringUtils.isEmpty(captureVariableName)) {
            errors.add("Capture Variable Name is required");
        } else  if (waitForCompletion && captureOutput && !VariableString.isValidVariableName(captureVariableName)) {
            errors.add("Capture Variable Name is invalid");
        }
        if (waitForCompletion && captureOutput && captureVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
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
        ruleOperation.setCommand(VariableString.getAsVariableString(command));
        ruleOperation.setInput(VariableString.getAsVariableString(input));
        ruleOperation.setWaitForCompletion(waitForCompletion);
        ruleOperation.setFailAfter(VariableString.getAsVariableString(failAfter));
        ruleOperation.setKillAfterFailure(killAfterFailure);
        ruleOperation.setFailOnNonZeroExitCode(failOnNonZeroExitCode);
        ruleOperation.setBreakAfterFailure(breakAfterFailure);
        ruleOperation.setCaptureOutput(captureOutput);
        ruleOperation.setCaptureAfterFailure(captureAfterFailure);
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
        return abbreviateTargetName(command);
    }

    @Override
    public RuleOperationModelType<ThenRunProcessModel, ThenRunProcess> getType() {
        return ThenModelType.RunProcess;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return captureOutput && StringUtils.isNotEmpty(captureVariableName) ?
                List.of(new VariableSourceEntry(captureVariableSource, List.of(captureVariableName))) :
                Collections.emptyList();
    }
}
