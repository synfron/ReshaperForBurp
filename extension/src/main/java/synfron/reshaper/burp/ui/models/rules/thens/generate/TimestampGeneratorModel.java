package synfron.reshaper.burp.ui.models.rules.thens.generate;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.TimestampGenerator;
import synfron.reshaper.burp.core.vars.VariableString;

@Getter
public class TimestampGeneratorModel extends GeneratorModel<TimestampGeneratorModel, TimestampGenerator> {

    private String format = "yyyy-MM-dd";
    private String minTimestamp = "";
    private String maxTimestamp = "";

    public TimestampGeneratorModel(TimestampGenerator generator) {
        super(generator);
        format = VariableString.toString(generator.getFormat(), format);
        minTimestamp = VariableString.toString(generator.getMinTimestamp(), minTimestamp);
        maxTimestamp = VariableString.toString(generator.getMaxTimestamp(), maxTimestamp);
    }

    public void setFormat(String format) {
        this.format = format;
        propertyChanged("format", format);
    }

    public void setMinTimestamp(String minTimestamp) {
        this.minTimestamp = minTimestamp;
        propertyChanged("minTimestamp", minTimestamp);
    }

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
