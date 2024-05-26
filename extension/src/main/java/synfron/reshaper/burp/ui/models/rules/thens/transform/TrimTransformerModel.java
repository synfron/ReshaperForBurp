package synfron.reshaper.burp.ui.models.rules.thens.transform;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TrimOption;
import synfron.reshaper.burp.core.rules.thens.entities.transform.TrimTransformer;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class TrimTransformerModel extends TransformerModel<TrimTransformerModel, TrimTransformer> {

    private TrimOption trimOption;
    private String characters = "";

    public TrimTransformerModel(TrimTransformer transformer) {
        super(transformer);
        trimOption = transformer.getTrimOption();
        characters = VariableString.toString(transformer.getCharacters(), characters);
    }

    public void setTrimOption(TrimOption trimOption) {
        this.trimOption = trimOption;
        propertyChanged("trimOption", trimOption);
    }

    public void setCharacters(String characters) {
        this.characters = characters;
        propertyChanged("characters", characters);
    }

    @Override
    public List<String> validate() {
        return super.validate();
    }

    @Override
    public boolean persist() {
        super.persist();
        transformer.setTrimOption(trimOption);
        transformer.setCharacters(VariableString.getAsVariableString(characters));
        return true;
    }
}
