package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.ThenLog;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenLogModel extends ThenModel<ThenLogModel, ThenLog> {

    @Getter
    private String text;

    public ThenLogModel(ThenLog then, Boolean isNew) {
        super(then, isNew);
        text = VariableString.getFormattedString(then.getText(), text);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(text)) {
            errors.add("Text is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setText(VariableString.getAsVariableString(text));
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
    public RuleOperationModelType<ThenLogModel, ThenLog> getType() {
        return ThenModelType.Log;
    }
}
