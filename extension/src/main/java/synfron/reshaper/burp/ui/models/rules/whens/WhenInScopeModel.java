package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenInScope;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class WhenInScopeModel extends WhenModel<WhenInScopeModel, WhenInScope> {

    @Getter
    private String url;

    public WhenInScopeModel(ProtocolType protocolType, WhenInScope when, Boolean isNew) {
        super(protocolType, when, isNew);
        url = VariableString.toString(when.getUrl(), url);
    }

    public void setUrl(String url) {
        this.url = url;
        propertyChanged("url", url);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (protocolType.accepts(ProtocolType.WebSocket) && StringUtils.isEmpty(url)) {
            errors.add("URL is required");
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setUrl(VariableString.getAsVariableString(url));
        return super.persist();
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(url);
    }

    @Override
    public RuleOperationModelType<WhenInScopeModel, WhenInScope> getType() {
        return WhenModelType.InScope;
    }
}
