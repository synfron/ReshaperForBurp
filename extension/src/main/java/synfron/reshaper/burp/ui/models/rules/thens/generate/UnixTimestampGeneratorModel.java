package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.UnixTimestampGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

@Getter
public class UnixTimestampGeneratorModel extends GeneratorModel<UnixTimestampGeneratorModel, UnixTimestampGenerator> implements IUnixTimestampGeneratorModel {

    private String format = "yyyy-MM-dd";
    private String minTimestamp = "";
    private String maxTimestamp = "";

    public UnixTimestampGeneratorModel(UnixTimestampGenerator generator) {
        super(generator);
        format = VariableString.toString(generator.getFormat(), format);
        minTimestamp = VariableString.toString(generator.getMinTimestamp(), minTimestamp);
        maxTimestamp = VariableString.toString(generator.getMaxTimestamp(), maxTimestamp);
    }
    
    @Override
    public void setFormat(String format) {
        this.format = format;
        propertyChanged("format", format);
    }

    @Override
    public void setMinTimestamp(String minTimestamp) {
        this.minTimestamp = minTimestamp;
        propertyChanged("minTimestamp", minTimestamp);
    }

    @Override
    public void setMaxTimestamp(String maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
        propertyChanged("maxTimestamp", maxTimestamp);
    }

    @Override
    public boolean persist() {
        super.persist();
        generator.setFormat(VariableString.getAsVariableString(format));
        generator.setMinTimestamp(VariableString.getAsVariableString(minTimestamp));
        generator.setMaxTimestamp(VariableString.getAsVariableString(maxTimestamp));
        return true;
    }
}
