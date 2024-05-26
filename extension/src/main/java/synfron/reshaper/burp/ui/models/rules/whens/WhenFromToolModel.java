package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.whens.WhenFromTool;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

public class WhenFromToolModel extends WhenModel<WhenFromToolModel, WhenFromTool> {

    @Getter
    private BurpTool tool;

    public WhenFromToolModel(ProtocolType protocolType, WhenFromTool when, Boolean isNew) {
        super(protocolType, when, isNew);
        tool = when.getTool();
    }

    public void setTool(BurpTool tool) {
        this.tool = tool;
        propertyChanged("tool", tool);
    }

    public boolean persist() {
        if (!validate().isEmpty()) {
            return false;
        }
        ruleOperation.setTool(tool);
        return super.persist();
    }

    @Override
    protected String getTargetName() {
        return tool.name();
    }

    @Override
    public RuleOperationModelType<WhenFromToolModel, WhenFromTool> getType() {
        return WhenModelType.FromTool;
    }
}
