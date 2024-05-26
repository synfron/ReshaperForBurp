package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.BytesGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
public class BytesGeneratorModel extends GeneratorModel<BytesGeneratorModel, BytesGenerator> implements IBytesGeneratorModel {

    private String length = "";
    private String encoding = StandardCharsets.ISO_8859_1.displayName();

    public BytesGeneratorModel(BytesGenerator generator) {
        super(generator);
        length = VariableString.toString(generator.getLength(), length);
        encoding = VariableString.toString(generator.getEncoding(), encoding);
    }

    @Override
    public void setLength(String length) {
        this.length = length;
        propertyChanged("length", length);
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
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
        generator.setEncoding(VariableString.getAsVariableString(encoding));
        return true;
    }
}
