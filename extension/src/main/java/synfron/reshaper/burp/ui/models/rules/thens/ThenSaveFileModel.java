package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenSaveFile;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenSaveFileModel extends ThenModel<ThenSaveFileModel, ThenSaveFile> {

    @Getter
    private String filePath;
    @Getter
    private String text;
    @Getter
    private String encoding;
    @Getter
    private FileExistsAction fileExistsAction;

    public ThenSaveFileModel(ProtocolType protocolType, ThenSaveFile then, Boolean isNew) {
        super(protocolType, then, isNew);
        filePath = VariableString.toString(then.getFilePath(), filePath);
        text = VariableString.toString(then.getText(), text);
        encoding = VariableString.toString(then.getEncoding(), encoding);
        fileExistsAction = then.getFileExistsAction();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        propertyChanged("filePath", filePath);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    public void setFileExistsAction(FileExistsAction fileExistsAction) {
        this.fileExistsAction = fileExistsAction;
        propertyChanged("fileExistsAction", fileExistsAction);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(filePath)) {
            errors.add("File Path is required");
        }
        if (!Encoder.isSupported(encoding) && !VariableTag.hasTag(encoding)) {
            errors.add("Unsupported encoding");
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setFilePath(VariableString.getAsVariableString(filePath));
        ruleOperation.setText(VariableString.getAsVariableString(text));
        ruleOperation.setEncoding(VariableString.getAsVariableString(encoding));
        ruleOperation.setFileExistsAction(fileExistsAction);
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(filePath);
    }

    @Override
    public RuleOperationModelType<ThenSaveFileModel, ThenSaveFile> getType() {
        return ThenModelType.SaveFile;
    }
}
