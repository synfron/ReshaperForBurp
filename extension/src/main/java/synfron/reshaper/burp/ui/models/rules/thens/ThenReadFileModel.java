package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.thens.ThenReadFile;
import synfron.reshaper.burp.core.rules.thens.ThenReadFile;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Collections;
import java.util.List;

public class ThenReadFileModel extends ThenModel<ThenReadFileModel, ThenReadFile> implements IVariableCreator {

    @Getter
    private String filePath;
    @Getter
    private String encoding;
    @Getter
    private boolean breakAfterFailure;
    @Getter
    private boolean captureAfterFailure;
    @Getter
    private VariableSource captureVariableSource;
    @Getter
    private String captureVariableName;
    @Getter
    private SetListItemPlacement itemPlacement;
    @Getter
    private String delimiter = "{{s:n}}";
    @Getter
    private String index;

    public ThenReadFileModel(ProtocolType protocolType, ThenReadFile then, Boolean isNew) {
        super(protocolType, then, isNew);
        filePath = VariableString.toString(then.getFilePath(), filePath);
        encoding = VariableString.toString(then.getEncoding(), encoding);
        breakAfterFailure = then.isBreakAfterFailure();
        captureAfterFailure = then.isCaptureAfterFailure();
        captureVariableSource = then.getCaptureVariableSource();
        captureVariableName = VariableString.toString(then.getCaptureVariableName(), captureVariableName);
        itemPlacement = then.getItemPlacement();
        delimiter = VariableString.toString(then.getDelimiter(), delimiter);
        index = VariableString.toString(then.getIndex(), index);
        VariableCreatorRegistry.register(this);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        propertyChanged("filePath", filePath);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        propertyChanged("encoding", encoding);
    }

    public void setBreakAfterFailure(boolean breakAfterFailure) {
        this.breakAfterFailure = breakAfterFailure;
        propertyChanged("breakAfterFailure", breakAfterFailure);
    }

    public void setCaptureAfterFailure(boolean captureAfterFailure) {
        this.captureAfterFailure = captureAfterFailure;
        propertyChanged("captureAfterFailure", captureAfterFailure);
    }


    public void setCaptureVariableSource(VariableSource captureVariableSource) {
        this.captureVariableSource = captureVariableSource;
        propertyChanged("captureVariableSource", captureVariableSource);
    }

    public void setCaptureVariableName(String captureVariableName) {
        this.captureVariableName = captureVariableName;
        propertyChanged("captureVariableName", captureVariableName);
    }

    public void setItemPlacement(SetListItemPlacement itemPlacement) {
        this.itemPlacement = itemPlacement;
        propertyChanged("itemPlacement", itemPlacement);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        propertyChanged("delimiter", delimiter);
    }

    public void setIndex(String index) {
        this.index = index;
        propertyChanged("index", index);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(filePath)) {
            errors.add("File Path is required");
        }
        if (!Encoder.isSupported(encoding) && !VariableString.hasTag(encoding)) {
            errors.add("Unsupported encoding");
        }
        if (StringUtils.isEmpty(captureVariableName)) {
            errors.add("Capture Variable Name is required");
        } else if (!VariableString.isValidVariableName(captureVariableName)) {
            errors.add("Capture Variable Name is invalid");
        }
        if (captureVariableSource.isList() && itemPlacement.isHasIndexSetter()) {
            if (StringUtils.isEmpty(index)) {
                errors.add("Index is required");
            } else if (!VariableString.isPotentialInt(index)) {
                errors.add("Index must be an integer");
            }
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setFilePath(VariableString.getAsVariableString(filePath));
        ruleOperation.setEncoding(VariableString.getAsVariableString(encoding));
        ruleOperation.setBreakAfterFailure(breakAfterFailure);
        ruleOperation.setCaptureAfterFailure(captureAfterFailure);
        ruleOperation.setCaptureVariableSource(captureVariableSource);
        ruleOperation.setCaptureVariableName(VariableString.getAsVariableString(captureVariableName));
        ruleOperation.setItemPlacement(itemPlacement);
        ruleOperation.setDelimiter(VariableString.getAsVariableString(delimiter));
        ruleOperation.setIndex(VariableString.getAsVariableString(index));
        setValidated(true);
        return true;
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(filePath);
    }

    @Override
    public RuleOperationModelType<ThenReadFileModel, ThenReadFile> getType() {
        return ThenModelType.ReadFile;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return StringUtils.isNotEmpty(captureVariableName) ?
                List.of(new VariableSourceEntry(captureVariableSource, captureVariableName)) :
                Collections.emptyList();
    }
}
