package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.utils.ValueGenerator;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IWordGeneratorModel;

import java.util.List;

@Getter
public class WordGeneratorVariableModel extends GeneratorVariableModel<WordGeneratorVariableModel> implements IWordGeneratorModel {

    private ValueGenerator.WordGeneratorType generatorType = ValueGenerator.WordGeneratorType.Word;
    private String count = "1";
    private String separator = "\\n";

    public void setGeneratorType(ValueGenerator.WordGeneratorType generatorType) {
        this.generatorType = generatorType;
        propertyChanged("generatorType", generatorType);
    }

    public void setCount(String count) {
        this.count = count;
        propertyChanged("count", count);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        propertyChanged("separator", separator);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (!StringUtils.isEmpty(count) && !TextUtils.isInt(count)) {
            errors.add("Count must be an integer");
        }
        return errors;
    }

    @Override
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Words.name().toLowerCase(), generatorType.name().toLowerCase(), count, TextUtils.jsonUnescape(separator));
    }
}
