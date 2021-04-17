package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenFromTool extends When<WhenFromTool> {
    @Getter
    @Setter
    private BurpTool tool = BurpTool.Proxy;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = eventInfo.getBurpTool() == tool;
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, MatchType.Equals, tool, null, eventInfo.getBurpTool(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenFromTool> getType() {
        return WhenType.FromTool;
    }
}
