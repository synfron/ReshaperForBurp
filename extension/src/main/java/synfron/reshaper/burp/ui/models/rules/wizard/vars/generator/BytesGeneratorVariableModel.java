package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IBytesGeneratorModel;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
public class BytesGeneratorVariableModel extends GeneratorVariableModel<BytesGeneratorVariableModel> implements IBytesGeneratorModel {

    private String length = "";
    private String encoding = StandardCharsets.ISO_8859_1.displayName();

    public void setLength(String length) {
        this.length = length;
        propertyChanged("length", length);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(length)) {
            errors.add("Length is required");
        } else if (!TextUtils.isInt(length)) {
            errors.add("Length must be an integer");
        }
        return errors;
    }

    @Override
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Bytes.name().toLowerCase(), length, encoding);
    }
}
