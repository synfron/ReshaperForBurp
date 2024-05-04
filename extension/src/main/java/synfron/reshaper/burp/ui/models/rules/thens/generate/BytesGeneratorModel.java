package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.BytesGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class BytesGeneratorModel extends GeneratorModel<BytesGeneratorModel, BytesGenerator> {

    private String length = "";

    public BytesGeneratorModel(BytesGenerator generator) {
        super(generator);
        length = VariableString.toString(generator.getLength(), length);
    }

    public void setLength(String length) {
        this.length = length;
        propertyChanged("length", length);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(length)) {
            errors.add("Length is required");
        } else if (!VariableString.isPotentialInt(length)) {
            errors.add("Length must be an integer");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setLength(VariableString.getAsVariableString(length));
        return true;
    }
}
