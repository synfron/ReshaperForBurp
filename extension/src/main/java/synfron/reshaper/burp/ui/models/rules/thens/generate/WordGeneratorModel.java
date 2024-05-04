package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.WordGenerator;
import synfron.reshaper.burp.core.rules.thens.entities.generate.WordGeneratorType;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class WordGeneratorModel extends GeneratorModel<WordGeneratorModel, WordGenerator> {

    private WordGeneratorType generatorType;
    private String count = "1";
    private String separator = "";

    public WordGeneratorModel(WordGenerator generator) {
        super(generator);
        generatorType = generator.getGeneratorType();
        count = VariableString.toString(generator.getCount(), count);
        separator = VariableString.toString(generator.getSeparator(), separator);
    }

    public void setGeneratorType(WordGeneratorType generatorType) {
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
        if (!StringUtils.isEmpty(count) && VariableString.isPotentialInt(count)) {
            errors.add("Count must be an integer");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setGeneratorType(generatorType);
        generator.setSeparator(VariableString.getAsVariableString(separator));
        generator.setCount(VariableString.getAsVariableString(count));
        return true;
    }
}
