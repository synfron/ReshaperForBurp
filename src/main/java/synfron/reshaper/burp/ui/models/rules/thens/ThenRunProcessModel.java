package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenRunProcess;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRunProcessModel extends ThenModel<ThenRunProcessModel, ThenRunProcess> {

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

    public ThenRunProcessModel(ThenRunProcess then, Boolean isNew) {
        super(then, isNew);
        command = VariableString.getFormattedString(then.getCommand(), command);
        input = VariableString.getFormattedString(then.getInput(), input);
        waitForCompletion = then.isWaitForCompletion();
        failAfter = VariableString.getFormattedString(then.getFailAfter(), failAfter);
        killAfterFailure = then.isKillAfterFailure();
        failOnNonZeroExitCode = then.isFailOnNonZeroExitCode();
        breakAfterFailure = then.isBreakAfterFailure();
        captureOutput = then.isCaptureOutput();
        captureAfterFailure = then.isCaptureAfterFailure();
        captureVariableSource = then.getCaptureVariableSource();
        captureVariableName = VariableString.getFormattedString(then.getCaptureVariableName(), captureVariableName);
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

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(command)) {
            errors.add("Command is required");
        }
        if (waitForCompletion && StringUtils.isEmpty(failAfter)) {
            errors.add("Fail After is required");
        }
        if (waitForCompletion && !VariableString.isPotentialInt(failAfter)) {
            errors.add("Fail After must be an integer");
        } else if (waitForCompletion && captureOutput && StringUtils.isEmpty(captureVariableName)) {
            errors.add("Capture Variable Name is required");
        } else  if (waitForCompletion && captureOutput && !VariableString.isValidVariableName(captureVariableName)) {
            errors.add("Capture Variable Name is invalid");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
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
    public RuleOperationModelType<ThenRunProcessModel, ThenRunProcess> getType() {
        return ThenModelType.RunProcess;
    }
}
