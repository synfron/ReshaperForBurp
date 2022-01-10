package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.CharsetUtils;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenSetEncoding;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenSetEncodingModel extends ThenModel<ThenSetEncodingModel, ThenSetEncoding> {

    @Getter
    private String encoding;

    public ThenSetEncodingModel(ThenSetEncoding then, Boolean isNew) {
        super(then, isNew);
        encoding = VariableString.toString(then.getEncoding(), encoding);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();

        if (!Encoder.isSupported(encoding) && !VariableString.hasTag(encoding)) {
            errors.add("Unsupported encoding");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setEncoding(VariableString.getAsVariableString(encoding));
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
    public RuleOperationModelType<ThenSetEncodingModel, ThenSetEncoding> getType() {
        return ThenModelType.SetEncoding;
    }
}
