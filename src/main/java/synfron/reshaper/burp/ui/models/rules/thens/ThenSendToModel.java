package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenSendTo;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenSendToModel extends ThenModel<ThenSendToModel, ThenSendTo> {

    @Getter
    private SendToOption sendTo;
    @Getter
    private boolean overrideDefaults;
    @Getter
    private String host;
    @Getter
    private String port;
    @Getter
    private String protocol;
    @Getter
    private String request;
    @Getter
    private String value;
    @Getter
    private String url;

    public ThenSendToModel(ThenSendTo then, Boolean isNew) {
        super(then, isNew);
        sendTo = then.getSendTo();
        overrideDefaults = then.isOverrideDefaults();
        host = VariableString.getFormattedString(then.getHost(), host);
        port = VariableString.getFormattedString(then.getPort(), port);
        protocol = VariableString.getFormattedString(then.getProtocol(), protocol);
        request = VariableString.getFormattedString(then.getRequest(), request);
        value = VariableString.getFormattedString(then.getValue(), value);
        url = VariableString.getFormattedString(then.getUrl(), url);
    }

    public void setSendTo(SendToOption sendTo) {
        this.sendTo = sendTo;
        propertyChanged("sendTo", sendTo);
    }

    public void setOverrideDefaults(boolean overrideDefaults) {
        this.overrideDefaults = overrideDefaults;
        propertyChanged("overrideDefaults", overrideDefaults);
    }

    public void setHost(String host) {
        this.host = host;
        propertyChanged("host", host);
    }

    public void setPort(String port) {
        this.port = port;
        propertyChanged("port", port);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
        propertyChanged("protocol", protocol);
    }

    public void setRequest(String request) {
        this.request = request;
        propertyChanged("request", request);
    }

    public void setValue(String value) {
        this.value = value;
        propertyChanged("value", value);
    }

    public void setUrl(String url) {
        this.url = url;
        propertyChanged("url", url);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (overrideDefaults) {
            switch (sendTo) {
                case Intruder:
                    validateIntruder(errors);
                    break;
                case Repeater:
                    validateRepeater(errors);
                    break;
            }
        }
        return errors;
    }

    private void validateIntruder(List<String> errors) {
        if (StringUtils.isNotEmpty(port) && !VariableString.isPotentialInt(port)) {
            errors.add("Port must be an integer");
        }
    }

    private void validateRepeater(List<String> errors) {
        if (StringUtils.isNotEmpty(port) && !VariableString.isPotentialInt(port)) {
            errors.add("Port must be an integer");
        }
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setSendTo(sendTo);
        ruleOperation.setOverrideDefaults(overrideDefaults);
        ruleOperation.setHost(VariableString.getAsVariableString(host));
        ruleOperation.setPort(VariableString.getAsVariableString(port));
        ruleOperation.setProtocol(VariableString.getAsVariableString(protocol));
        ruleOperation.setRequest(VariableString.getAsVariableString(request));
        ruleOperation.setValue(VariableString.getAsVariableString(value));
        ruleOperation.setUrl(VariableString.getAsVariableString(url));
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
    public RuleOperationModelType<ThenSendToModel, ThenSendTo> getType() {
        return ThenModelType.SendTo;
    }
}
