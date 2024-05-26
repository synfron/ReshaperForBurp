package synfron.reshaper.burp.ui.models.rules.thens.generate;

public interface IBytesGeneratorModel extends IGeneratorModel<IBytesGeneratorModel> {
    void setLength(String length);

    void setEncoding(String encoding);

    String getLength();

    String getEncoding();
}
