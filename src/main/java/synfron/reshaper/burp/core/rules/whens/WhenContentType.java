package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.ContentType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenContentType extends When<WhenContentType> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private ContentType contentType = ContentType.Json;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        ContentType eventContentType = eventInfo.getHttpRequestMessage().getContentType();
        boolean isMatch = eventContentType != null && this.contentType.hasFlags(eventContentType);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Contains, contentType, eventContentType, isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenContentType> getType() {
        return WhenType.ContentType;
    }
}
