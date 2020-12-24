package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenFromTool extends When<WhenFromTool> {
    @Getter
    @Setter
    private BurpTool tool = BurpTool.Proxy;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        return eventInfo.getBurpTool() == tool;
    }

    @Override
    public RuleOperationType<WhenFromTool> getType() {
        return WhenType.FromTool;
    }
}
