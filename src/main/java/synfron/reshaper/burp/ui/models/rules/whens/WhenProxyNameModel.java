package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenProxyName;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class WhenProxyNameModel extends WhenModel<WhenProxyNameModel, WhenProxyName> {

    @Getter
    private String proxyName;

    public WhenProxyNameModel(ProtocolType protocolType, WhenProxyName when, Boolean isNew) {
        super(protocolType, when, isNew);
        proxyName = when.getProxyName();
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
        propertyChanged("proxyName", proxyName);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(proxyName)) {
            errors.add("Proxy Name is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setProxyName(proxyName);
        return super.persist();
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
    protected String getTargetName() {
        return abbreviateTargetName(proxyName);
    }

    @Override
    public RuleOperationModelType<WhenProxyNameModel, WhenProxyName> getType() {
        return WhenModelType.ProxyName;
    }
}
