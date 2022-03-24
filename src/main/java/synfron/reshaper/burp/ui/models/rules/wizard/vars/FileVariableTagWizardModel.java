package synfron.reshaper.burp.ui.models.rules.wizard.vars;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.util.ArrayList;
import java.util.List;

public class FileVariableTagWizardModel implements IVariableTagWizardModel {

    @Getter
    private String encoding = Encoder.getDefaultEncoderName();

    @Getter
    private String filePath;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        propertyChanged("filePath", filePath);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(encoding)) {
            errors.add("Encoding is required");
        }
        if (StringUtils.isEmpty(filePath)) {
            errors.add("File Path is required");
        }
        return errors;
    }

    @Override
    public VariableSource getVariableSource() {
        return VariableSource.File;
    }

    @Override
    public String getTag() {
        return validate().isEmpty() ?
                VariableSourceEntry.getShortTag(VariableSource.File, encoding, filePath) :
                null;
    }
}
