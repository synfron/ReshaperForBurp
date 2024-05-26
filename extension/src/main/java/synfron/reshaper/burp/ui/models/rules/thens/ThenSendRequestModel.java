package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenSendRequest;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenSendRequestModel extends ThenModel<ThenSendRequestModel, ThenSendRequest> implements IVariableCreator {

    @Getter
    private String request;
    @Getter
    private String url;
    @Getter
    private String protocol;
    @Getter
    private String address;
    @Getter
    private String port;
    @Getter
    private boolean waitForCompletion;
    @Getter
    private String failAfter = "5000";
    @Getter
    private boolean failOnErrorStatusCode;
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

    public ThenSendRequestModel(ProtocolType protocolType, ThenSendRequest then, Boolean isNew) {
        super(protocolType, then, isNew);
        request = VariableString.toString(then.getRequest(), request);
        url = VariableString.toString(then.getUrl(), url);
        protocol = VariableString.toString(then.getProtocol(), protocol);
        address = VariableString.toString(then.getAddress(), address);
        port = VariableString.toString(then.getPort(), port);
        waitForCompletion = then.isWaitForCompletion();
        failAfter = VariableString.toString(then.getFailAfter(), failAfter);
        failOnErrorStatusCode = then.isFailOnErrorStatusCode();
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

    public void setRequest(String request) {
        this.request = request;
        propertyChanged("request", request);
    }

    public void setUrl(String url) {
        this.url = url;
        propertyChanged("url", url);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
        propertyChanged("protocol", protocol);
    }

    public void setAddress(String address) {
        this.address = address;
        propertyChanged("address", address);
    }

    public void setPort(String port) {
        this.port = port;
        propertyChanged("port", port);
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
        propertyChanged("waitForCompletion", waitForCompletion);
    }

    public void setFailAfter(String failAfter) {
        this.failAfter = failAfter;
        propertyChanged("failAfter", failAfter);
    }

    public void setFailOnErrorStatusCode(boolean failOnErrorStatusCode) {
        this.failOnErrorStatusCode = failOnErrorStatusCode;
        propertyChanged("failOnNonZeroExitCode", failOnErrorStatusCode);
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
        if (StringUtils.isNotEmpty(port) && !VariableString.isPotentialInt(port)) {
            errors.add("Port must be an integer");
        }
        if (waitForCompletion && StringUtils.isEmpty(failAfter)) {
            errors.add("Fail After is required");
        } else if (waitForCompletion && !VariableString.isPotentialInt(failAfter)) {
            errors.add("Fail After must be an integer");
        }
        if (waitForCompletion && captureOutput && StringUtils.isEmpty(captureVariableName)) {
            errors.add("Capture Variable Name is required");
        } else if (waitForCompletion && captureOutput && !VariableString.isValidVariableName(captureVariableName)) {
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
        ruleOperation.setRequest(VariableString.getAsVariableString(request));
        ruleOperation.setUrl(VariableString.getAsVariableString(url));
        ruleOperation.setProtocol(VariableString.getAsVariableString(protocol));
        ruleOperation.setAddress(VariableString.getAsVariableString(address));
        ruleOperation.setPort(VariableString.getAsVariableString(port));
        ruleOperation.setWaitForCompletion(waitForCompletion);
        ruleOperation.setFailAfter(VariableString.getAsVariableString(failAfter));
        ruleOperation.setFailOnErrorStatusCode(failOnErrorStatusCode);
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
    public RuleOperationModelType<ThenSendRequestModel, ThenSendRequest> getType() {
        return ThenModelType.SendRequest;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return captureOutput && StringUtils.isNotEmpty(captureVariableName) ?
                List.of(new VariableSourceEntry(captureVariableSource, List.of(captureVariableName))) :
                Collections.emptyList();
    }
}
