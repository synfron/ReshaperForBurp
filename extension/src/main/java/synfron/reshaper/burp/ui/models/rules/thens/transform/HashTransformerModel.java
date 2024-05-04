package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.HashTransformer;
import synfron.reshaper.burp.core.rules.thens.entities.transform.HashType;

import java.util.List;

@Getter
public class HashTransformerModel extends TransformerModel<HashTransformerModel, HashTransformer> {

    private HashType hashType;

    public HashTransformerModel(HashTransformer transformer) {
        super(transformer);
        hashType = transformer.getHashType();
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
        propertyChanged("hashType", hashType);
    }

    @Override
    public List<String> validate() {
        return super.validate();
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setHashType(hashType);
        return true;
    }
}
