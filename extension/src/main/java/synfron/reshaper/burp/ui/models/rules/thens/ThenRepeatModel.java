package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.thens.ThenRepeat;
import synfron.reshaper.burp.core.rules.thens.entities.repeat.RepeatCondition;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableSourceEntry;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.VariableTag;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class ThenRepeatModel extends ThenModel<ThenRepeatModel, ThenRepeat> implements IVariableCreator {

    @Getter
    private String subGroupCount;
    @Getter
    private RepeatCondition repeatCondition;
    @Getter
    private VariableSource listVariableSource;
    @Getter
    private String listVariableName;
    @Getter
    private String entryVariableName;
    @Getter
    private String count;
    @Getter
    private String booleanValue;
    @Getter
    private String maxCount;

    public ThenRepeatModel(ProtocolType protocolType, ThenRepeat then, Boolean isNew) {
        super(protocolType, then, isNew);
        subGroupCount = Integer.toString(then.getSubGroupCount());
        repeatCondition = then.getRepeatCondition();
        listVariableSource = then.getListVariableSource();
        listVariableName = VariableString.toString(then.getListVariableName(), listVariableName);
        entryVariableName = VariableString.toString(then.getEntryVariableName(), entryVariableName);
        booleanValue = VariableString.toString(then.getBooleanValue(), booleanValue);
        count = VariableString.toString(then.getCount(), count);
        maxCount = Integer.toString(then.getMaxCount());
        VariableCreatorRegistry.register(this);
    }

    public void setSubGroupCount(String subGroupCount) {
        this.subGroupCount = subGroupCount;
        propertyChanged("subGroupCount", subGroupCount);
    }

    public void setRepeatCondition(RepeatCondition repeatCondition) {
        this.repeatCondition = repeatCondition;
        propertyChanged("repeatCondition", repeatCondition);
    }

    public void setCount(String count) {
        this.count = count;
        propertyChanged("count", count);
    }

    public void setBooleanValue(String booleanValue) {
        this.booleanValue = booleanValue;
        propertyChanged("booleanValue", booleanValue);
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

    public void setMaxCount(String maxCount) {
        this.maxCount = maxCount;
        propertyChanged("maxCount", maxCount);
    }

    public List<String> validate() {
        List<String> errors = super.validate();
        if (StringUtils.isEmpty(subGroupCount)) {
            errors.add("Number of Following Thens Included is required");
        } else if (!TextUtils.isInt(subGroupCount)) {
            errors.add("Number of Following Thens Included must be a integer");
        } else if (Integer.parseInt(subGroupCount) <= 0) {
            errors.add("Number of Following Thens Included must be greater than 0");
        }
        switch (repeatCondition) {
            case Count -> {
                if (StringUtils.isEmpty(count)) {
                    errors.add("Count is required");
                } else if (!VariableString.isPotentialInt(count)) {
                    errors.add("Count must be an integer");
                }
            }
            case HasNextItem -> {
                if (StringUtils.isEmpty(listVariableName)) {
                    errors.add("List Variable Name is required");
                }
                if (StringUtils.isEmpty(entryVariableName)) {
                    errors.add("Item Event Variable Name is required");
                }
            }
            case WhileTrue -> {
                if (StringUtils.isEmpty(booleanValue)) {
                    errors.add("Boolean Value is required");
                } else if (!VariableString.getAsVariableString(booleanValue).hasVariables()) {
                    errors.add("Boolean Value must contain a variable tag");
                }
                if (StringUtils.isEmpty(maxCount)) {
                    errors.add("Max Count is required");
                } else if (!TextUtils.isInt(maxCount)) {
                    errors.add("Max Count must be a integer");
                } else if (Integer.parseInt(maxCount) <= 0) {
                    errors.add("Max Count must be greater than 0");
                }
            }
        }
        return errors;
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setSubGroupCount(Integer.parseInt(subGroupCount));
        ruleOperation.setRepeatCondition(repeatCondition);
        ruleOperation.setListVariableSource(listVariableSource);
        ruleOperation.setListVariableName(VariableString.getAsVariableString(listVariableName));
        ruleOperation.setEntryVariableName(VariableString.getAsVariableString(entryVariableName));
        ruleOperation.setCount(VariableString.getAsVariableString(count));
        ruleOperation.setBooleanValue(VariableString.getAsVariableString(booleanValue));
        ruleOperation.setMaxCount(Integer.parseInt(maxCount));
        setValidated(true);
        return true;
    }

    @Override
    public List<VariableSourceEntry> getVariableEntries() {
        return repeatCondition == RepeatCondition.HasNextItem ?
                List.of(new VariableSourceEntry(VariableSource.Event, List.of(entryVariableName))) :
                List.of();
    }

    @Override
    protected String getTargetName() {
        return abbreviateTargetName(switch (repeatCondition) {
            case Count -> count;
            case HasNextItem -> VariableTag.getTag(listVariableSource, listVariableName);
            case WhileTrue -> booleanValue;
        });
    }

    @Override
    public RuleOperationModelType<ThenRepeatModel, ThenRepeat> getType() {
        return ThenModelType.Repeat;
    }
}
