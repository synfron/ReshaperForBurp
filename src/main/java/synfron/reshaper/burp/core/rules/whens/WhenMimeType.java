package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;

public class WhenMimeType extends When<WhenMimeType> {
    @Getter @Setter
    private MimeType mimeType = MimeType.Html;

    @Override
    public boolean isMatch(IEventInfo eventInfo) {
        MimeType eventMimeType = eventInfo.getHttpResponseMessage().getMimeType();
        boolean isMatch = eventMimeType != null && this.mimeType.hasFlags(eventMimeType);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(this, null, MatchType.Contains, mimeType, eventMimeType, isMatch);
        return isMatch;
    }

    @Override
    public RuleOperationType<WhenMimeType> getType() {
        return WhenType.MimeType;
    }
}
