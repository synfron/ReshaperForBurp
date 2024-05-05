package synfron.reshaper.burp.ui.models.rules.thens.generate;

public interface ITimestampGeneratorModel extends IGeneratorModel<ITimestampGeneratorModel> {
    void setFormat(String format);

    void setMinTimestamp(String minTimestamp);

    void setMaxTimestamp(String maxTimestamp);

    String getFormat();

    String getMinTimestamp();

    String getMaxTimestamp();
}
