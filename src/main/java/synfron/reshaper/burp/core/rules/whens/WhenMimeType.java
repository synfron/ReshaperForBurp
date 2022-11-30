package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenMimeType extends When<WhenMimeType> implements IHttpRuleOperation {
    @Getter @Setter
    private MimeType mimeType = MimeType.Html;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        MimeType eventMimeType = ((HttpEventInfo)eventInfo).getHttpResponseMessage().getMimeType();
        boolean isMatch = eventMimeType != null && this.mimeType.hasFlags(eventMimeType);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Contains, mimeType, eventMimeType, isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenMimeType> getType() {
        return WhenType.MimeType;
    }
}
