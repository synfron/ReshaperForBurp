package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.ThenSaveFile;
import synfron.reshaper.burp.core.rules.thens.entities.savefile.FileExistsAction;
import synfron.reshaper.burp.core.vars.VariableString;
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

    public ThenSaveFileModel(ThenSaveFile then, Boolean isNew) {
        super(then, isNew);
        filePath = VariableString.getTag(then.getFilePath(), filePath);
        text = VariableString.getTag(then.getText(), text);
        encoding = VariableString.getTag(then.getEncoding(), encoding);
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
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
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
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    public RuleOperationModelType<ThenSaveFileModel, ThenSaveFile> getType() {
        return ThenModelType.SaveFile;
    }
}
