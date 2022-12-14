package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRunScript;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRunScriptModel extends ThenModel<ThenRunScriptModel, ThenRunScript> {

    @Getter
    private String script;
    @Getter
    private String maxExecutionSeconds;

    public ThenRunScriptModel(ProtocolType protocolType, ThenRunScript then, Boolean isNew) {
        super(protocolType, then, isNew);
        script = then.getScript();
        maxExecutionSeconds = TextUtils.toString(then.getMaxExecutionSeconds());
    }

    public void setScript(String script) {
        this.script = script;
        propertyChanged("script", script);
    }

    public void setMaxExecutionSeconds(String maxExecutionSeconds) {
        this.maxExecutionSeconds = maxExecutionSeconds;
        propertyChanged("maxExecutionSeconds", maxExecutionSeconds);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(script)) {
            errors.add("Script is required");
        }
        if (!TextUtils.isInt(maxExecutionSeconds)) {
            errors.add("Max Execution (secs) must be an integer");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setScript(script);
        ruleOperation.setMaxExecutionSeconds(Integer.parseInt(maxExecutionSeconds));
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
    protected String getTargetName() {
        return abbreviateTargetName(script);
    }

    @Override
    public RuleOperationModelType<ThenRunScriptModel, ThenRunScript> getType() {
        return ThenModelType.RunScript;
    }
}
