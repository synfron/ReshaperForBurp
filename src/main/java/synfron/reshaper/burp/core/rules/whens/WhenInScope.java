package synfron.reshaper.burp.core.rules.whens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.vars.VariableString;

public class WhenInScope extends When<WhenInScope> implements IHttpRuleOperation, IWebSocketRuleOperation {


    @Getter
    @Setter
    private VariableString url;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = false;
        String url = VariableString.getTextOrDefault(eventInfo, this.url, eventInfo.getUrl());
        try {
            isMatch = BurpExtender.getApi().scope().isInScope(url);
        } catch (Exception ignored) {
        }
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, isMatch, url);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenInScope> getType() {
        return WhenType.InScope;
    }
}
