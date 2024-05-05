package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IIntegerGeneratorModel;

import java.util.List;

@Getter
public class IntegerGeneratorVariableModel extends GeneratorVariableModel<IntegerGeneratorVariableModel> implements IIntegerGeneratorModel {
    private String minValue = "0";
    private String maxValue = "10";
    private String base = "10";

    public void setBase(String base) {
        this.base = base;
        propertyChanged("base", base);
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        propertyChanged("maxValue", maxValue);
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
        propertyChanged("minValue", minValue);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(minValue)) {
            errors.add("Min Value is required");
        } else if (!TextUtils.isLong(minValue)) {
            errors.add("Min Value must be a long integer");
        }
        if (StringUtils.isEmpty(maxValue)) {
            errors.add("Max Value is required");
        } else if (!TextUtils.isLong(maxValue)) {
            errors.add("Max Value must be a long integer");
        }
        if (!StringUtils.isEmpty(base) && !TextUtils.isInt(base)) {
            errors.add("Base must be an integer");
        }
        return errors;
    }

    @Override
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.Integer.name().toLowerCase(), minValue, maxValue, base);
    }
}
