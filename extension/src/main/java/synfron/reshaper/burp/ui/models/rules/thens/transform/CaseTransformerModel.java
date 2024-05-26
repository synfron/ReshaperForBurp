package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.CaseTransformer;
import synfron.reshaper.burp.core.utils.PhraseCase;

import java.util.List;

@Getter
public class CaseTransformerModel extends TransformerModel<CaseTransformerModel, CaseTransformer> {

    private PhraseCase phraseCase;

    public CaseTransformerModel(CaseTransformer transformer) {
        super(transformer);
        phraseCase = transformer.getPhraseCase();
    }

    public void setPhraseCase(PhraseCase phraseCase) {
        this.phraseCase = phraseCase;
        propertyChanged("phraseCase", phraseCase);
    }

    @Override
    public List<String> validate() {
        return super.validate();
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setPhraseCase(phraseCase);
        return true;
    }
}
