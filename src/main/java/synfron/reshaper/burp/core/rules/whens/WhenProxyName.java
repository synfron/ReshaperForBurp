package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenProxyName extends When<WhenProxyName> {
    @Getter @Setter
    private String proxyName;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        return eventInfo.getProxyName().equals(proxyName);
    }

    @Override
    public RuleOperationType<WhenProxyName> getType() {
        return WhenType.ProxyName;
    }
}
