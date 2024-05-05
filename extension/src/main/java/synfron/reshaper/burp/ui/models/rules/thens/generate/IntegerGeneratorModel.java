package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IntegerGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

@Getter
public class IntegerGeneratorModel extends GeneratorModel<IntegerGeneratorModel, IntegerGenerator> implements IIntegerGeneratorModel {
    private String minValue = "0";
    private String maxValue = "10";
    private String base = "10";

    public IntegerGeneratorModel(IntegerGenerator generator) {
        super(generator);
        minValue = VariableString.toString(generator.getMinValue(), minValue);
        maxValue = VariableString.toString(generator.getMaxValue(), maxValue);
        base = VariableString.toString(generator.getBase(), base);
    }

    @Override
    public void setBase(String base) {
        this.base = base;
        propertyChanged("base", base);
    }

    @Override
    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        propertyChanged("maxValue", maxValue);
    }

    @Override
    public void setMinValue(String minValue) {
        this.minValue = minValue;
        propertyChanged("minValue", minValue);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(minValue)) {
            errors.add("Min Value is required");
        } else if (!VariableString.isPotentialLong(minValue)) {
            errors.add("Min Value must be a long integer");
        }
        if (StringUtils.isEmpty(maxValue)) {
            errors.add("Max Value is required");
        } else if (!VariableString.isPotentialLong(maxValue)) {
            errors.add("Max Value must be a long integer");
        }
        if (!StringUtils.isEmpty(base) && !VariableString.isPotentialInt(base)) {
            errors.add("Base must be an integer");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setMinValue(VariableString.getAsVariableString(minValue));
        generator.setMaxValue(VariableString.getAsVariableString(maxValue));
        generator.setBase(VariableString.getAsVariableString(base));
        return true;
    }
}
