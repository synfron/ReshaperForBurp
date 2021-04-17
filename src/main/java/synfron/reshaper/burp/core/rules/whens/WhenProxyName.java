package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenProxyName extends When<WhenProxyName> {
    @Getter @Setter
    private String proxyName;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = StringUtils.equalsIgnoreCase(eventInfo.getProxyName(), proxyName);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, MatchType.Equals, proxyName, null, eventInfo.getProxyName(), isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenProxyName> getType() {
        return WhenType.ProxyName;
    }
}
