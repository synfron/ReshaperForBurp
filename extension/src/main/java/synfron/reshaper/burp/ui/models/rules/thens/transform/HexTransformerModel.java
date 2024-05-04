package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.HexTransformer;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TextTransform;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class HexTransformerModel extends TransformerModel<HexTransformerModel, HexTransformer> {

    private TextTransform action;
    private String encoding = "";

    public HexTransformerModel(HexTransformer transformer) {
        super(transformer);
        action = transformer.getAction();
        encoding = VariableString.toString(transformer.getEncoding(), encoding);
    }

    public void setAction(TextTransform action) {
        this.action = action;
        propertyChanged("action", action);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setAction(action);
        transformer.setEncoding(VariableString.getAsVariableString(encoding));
        return true;
    }
}
