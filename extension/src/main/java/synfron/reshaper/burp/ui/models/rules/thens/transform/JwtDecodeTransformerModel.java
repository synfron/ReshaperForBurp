package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.JwtDecodeTransformer;
import synfron.reshaper.burp.core.rules.thens.entities.transform.JwtSegment;

import java.util.List;

@Getter
public class JwtDecodeTransformerModel extends TransformerModel<JwtDecodeTransformerModel, JwtDecodeTransformer> {

    private JwtSegment segment;

    public JwtDecodeTransformerModel(JwtDecodeTransformer transformer) {
        super(transformer);
        segment = transformer.getSegment();
    }

    public void setSegment(JwtSegment segment) {
        this.segment = segment;
        propertyChanged("segment", segment);
    }

    @Override
    public List<String> validate() {
        return super.validate();
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setSegment(segment);
        return true;
    }
}
