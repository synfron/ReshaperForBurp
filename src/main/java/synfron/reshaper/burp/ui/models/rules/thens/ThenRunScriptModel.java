package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenRunScript;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRunScriptModel extends ThenModel<ThenRunScriptModel, ThenRunScript> {

    @Getter
    private String script;

    public ThenRunScriptModel(ThenRunScript then, Boolean isNew) {
        super(then, isNew);
        script = then.getScript();
    }

    public void setScript(String script) {
        this.script = script;
        propertyChanged("script", script);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(script)) {
            errors.add("Script is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setScript(script);
        setSaved(true);
        return true;
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setSaved(true);
        return true;
    }

    @Override
    public RuleOperationModelType<ThenRunScriptModel, ThenRunScript> getType() {
        return ThenModelType.RunScript;
    }
}
