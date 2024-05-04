package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EncodeTransform;
import synfron.reshaper.burp.core.rules.thens.entities.transform.EncoderType;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TextEncodeTransformer;

import java.util.List;

@Getter
public class TextEncodeTransformerModel extends TransformerModel<TextEncodeTransformerModel, TextEncodeTransformer> {

    private EncoderType encoderType;
    private EncodeTransform action;

    public TextEncodeTransformerModel(TextEncodeTransformer transformer) {
        super(transformer);
        encoderType = transformer.getEncoderType();
        action = transformer.getAction();
    }

    public void setEncoderType(EncoderType encoderType) {
        this.encoderType = encoderType;
        propertyChanged("encoderType", encoderType);
    }

    public void setAction(EncodeTransform action) {
        this.action = action;
        propertyChanged("action", action);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setEncoderType(encoderType);
        transformer.setAction(action);
        return true;
    }
}
