package synfron.reshaper.burp.core.rules.whens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.vars.VariableString;

import java.net.MalformedURLException;
import java.net.URL;

public class WhenInScope extends When<WhenInScope> {


    @Getter
    @Setter
    private VariableString url;

    @Override
    public boolean isMatch(IEventInfo eventInfo) {
        boolean isMatch = false;
        String url = VariableString.getTextOrDefault(eventInfo, this.url, eventInfo.getUrl());
        try {
            isMatch = BurpExtender.getCallbacks().isInScope(new URL(url));
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
