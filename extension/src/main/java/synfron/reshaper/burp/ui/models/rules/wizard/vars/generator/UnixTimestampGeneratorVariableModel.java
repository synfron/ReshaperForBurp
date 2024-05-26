package synfron.reshaper.burp.ui.models.rules.wizard.vars.generator;

import lombok.Getter;
import synfron.reshaper.burp.core.rules.thens.entities.generate.GenerateOption;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.thens.generate.IUnixTimestampGeneratorModel;

@Getter
public class UnixTimestampGeneratorVariableModel extends GeneratorVariableModel<UnixTimestampGeneratorVariableModel> implements IUnixTimestampGeneratorModel {

    private String format = "yyyy-MM-dd";
    private String minTimestamp = "";
    private String maxTimestamp = "";
    
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
    public String getTagInternal() {
        return VariableTag.getShortTag(VariableSource.Generator, GenerateOption.UnixTimestamp.name().toLowerCase(), format, minTimestamp, maxTimestamp);
    }
}
