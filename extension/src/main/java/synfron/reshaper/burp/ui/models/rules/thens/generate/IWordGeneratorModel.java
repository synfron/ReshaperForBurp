package synfron.reshaper.burp.ui.models.rules.thens.generate;

import synfron.reshaper.burp.core.utils.ValueGenerator;

public interface IWordGeneratorModel extends IGeneratorModel<IWordGeneratorModel> {
    void setGeneratorType(ValueGenerator.WordGeneratorType generatorType);

    void setCount(String count);

    void setSeparator(String separator);

    ValueGenerator.WordGeneratorType getGeneratorType();

    String getCount();

    String getSeparator();
}
