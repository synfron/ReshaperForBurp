package synfron.reshaper.burp.ui.models.rules.thens.generate;

public interface IIntegerGeneratorModel extends IGeneratorModel<IIntegerGeneratorModel> {
    void setBase(String base);

    void setMaxValue(String maxValue);

    void setMinValue(String minValue);

    String getMinValue();

    String getMaxValue();

    String getBase();
}
