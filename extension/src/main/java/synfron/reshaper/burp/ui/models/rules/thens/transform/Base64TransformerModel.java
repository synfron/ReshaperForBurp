package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.Base64Transformer;
import synfron.reshaper.burp.core.rules.thens.entities.transform.Base64Variant;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EncodeTransform;
import synfron.reshaper.burp.core.vars.VariableString;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
public class Base64TransformerModel extends TransformerModel<Base64TransformerModel, Base64Transformer> {

    private Base64Variant variant;
    private EncodeTransform action;
    private String encoding = StandardCharsets.ISO_8859_1.displayName();

    public Base64TransformerModel(Base64Transformer transformer) {
        super(transformer);
        variant = transformer.getVariant();
        action = transformer.getAction();
        encoding = VariableString.toString(transformer.getEncoding(), encoding);
    }

    public void setVariant(Base64Variant variant) {
        this.variant = variant;
        propertyChanged("variant", variant);
    }

    public void setAction(EncodeTransform action) {
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
        transformer.setVariant(variant);
        transformer.setAction(action);
        transformer.setEncoding(VariableString.getAsVariableString(encoding));
        return true;
    }
}
