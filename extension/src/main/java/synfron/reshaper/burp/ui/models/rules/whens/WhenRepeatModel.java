package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenRepeat;
import synfron.reshaper.burp.core.rules.whens.entities.repeat.SuccessCriteria;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.thens.IVariableCreator;
import synfron.reshaper.burp.ui.models.rules.thens.VariableCreatorRegistry;

import java.util.List;

public class WhenRepeatModel extends WhenModel<WhenRepeatModel, WhenRepeat> implements IVariableCreator {

    @Getter
    private String subGroupCount;
    @Getter
    private SuccessCriteria successCriteria;
    @Getter
    private VariableSource listVariableSource;
    @Getter
    private String listVariableName;
    @Getter
    private String entryVariableName;

    public WhenRepeatModel(ProtocolType protocolType, WhenRepeat when, Boolean isNew) {
        super(protocolType, when, isNew);
        subGroupCount = Integer.toString(when.getSubGroupCount());
        successCriteria = when.getSuccessCriteria();
        listVariableSource = when.getListVariableSource();
        listVariableName = VariableString.toString(when.getListVariableName(), listVariableName);
        entryVariableName = VariableString.toString(when.getEntryVariableName(), entryVariableName);
        VariableCreatorRegistry.register(this);
    }

    public void setSubGroupCount(String subGroupCount) {
        this.subGroupCount = subGroupCount;
        propertyChanged("subGroupCount", subGroupCount);
    }

    public void setSuccessCriteria(SuccessCriteria successCriteria) {
        this.successCriteria = successCriteria;
        propertyChanged("successCriteria", successCriteria);
    }

    public void setListVariableSource(VariableSource listVariableSource) {
        this.listVariableSource = listVariableSource;
        propertyChanged("listVariableSource", listVariableSource);
    }

    public void setListVariableName(String listVariableName) {
        this.listVariableName = listVariableName;
        propertyChanged("listVariableName", listVariableName);
    }

    public void setEntryVariableName(String entryVariableName) {
        this.entryVariableName = entryVariableName;
        propertyChanged("entryVariableName", entryVariableName);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(subGroupCount)) {
            errors.add("Number of Following Whens Included is required");
        } else if (!TextUtils.isInt(subGroupCount)) {
            errors.add("Number of Following Whens Included must be a integer");
        } else if (Integer.parseInt(subGroupCount) <= 0) {
            errors.add("Number of Following Thens Included must be greater than 0");
        }
        if (StringUtils.isEmpty(listVariableName)) {
            errors.add("List Variable Name is required");
        }
        if (StringUtils.isEmpty(entryVariableName)) {
            errors.add("Item Event Variable Name is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setSubGroupCount(Integer.parseInt(subGroupCount));
        ruleOperation.setSuccessCriteria(successCriteria);
        ruleOperation.setListVariableSource(listVariableSource);
        ruleOperation.setListVariableName(VariableString.getAsVariableString(listVariableName));
        ruleOperation.setEntryVariableName(VariableString.getAsVariableString(entryVariableName));
        return super.persist();
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
    protected String getTargetName() {
        return abbreviateTargetName(VariableSourceEntry.getTag(listVariableSource, listVariableName));
    }

    @Override
    public RuleOperationModelType<WhenRepeatModel, WhenRepeat> getType() {
        return WhenModelType.Repeat;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return List.of(new VariableSourceEntry(VariableSource.Event, entryVariableName));
    }
}
