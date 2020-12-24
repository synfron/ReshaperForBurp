package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

public class WhenMatchesText extends When<WhenMatchesText> {
    @Getter
    @Setter
    private VariableString identifier;
    @Getter
    @Setter
    private VariableString sourceText;
    @Getter
    @Setter
    private VariableString matchText;
    @Getter
    @Setter
    private MessageValue messageValue = MessageValue.HttpRequestBody;
    @Getter
    @Setter
    private MessageValueType messageValueType = MessageValueType.Text;
    @Getter
    @Setter
    private VariableString messageValuePath;
    @Getter
    @Setter
    private MatchType matchType = MatchType.Equals;
    @Getter
    @Setter
    public boolean useMessageValue = true;

    @Override
    public boolean isMatch(EventInfo eventInfo) {
        boolean isMatch = false;
        try {
            String sourceText = useMessageValue ?
                    MessageValueHandler.getValue(eventInfo, messageValue, identifier) :
                    this.sourceText.getText(eventInfo);
            sourceText = getPathValue(sourceText, eventInfo);
            String matchText = this.matchText.getText(eventInfo);

            switch (matchType) {
                case BeginsWith:
                    isMatch = sourceText.startsWith(matchText);
                    break;
                case EndsWith:
                    isMatch = sourceText.endsWith(matchText);
                    break;
                case Contains:
                    isMatch = sourceText.contains(matchText);
                    break;
                case Equals:
                    isMatch = sourceText.endsWith(matchText);
                    break;
                case Regex:
                    isMatch = TextUtils.isMatch(sourceText, matchText);
                    break;
            }
        } catch (Exception ignored) {
        }
        return isMatch;
    }

    private String getPathValue(String value, EventInfo eventInfo) {
        if (messageValueType != MessageValueType.Text && messageValuePath != null)
        {
            switch (messageValueType)
            {
                case Json:
                    value = TextUtils.getJsonValue(value, messageValuePath.getText(eventInfo));
                    break;
                case Html:
                    value = TextUtils.getHtmlValue(value, messageValuePath.getText(eventInfo));
                    break;
            }
        }
        return value;
    }

    @Override
    public RuleOperationType<WhenMatchesText> getType() {
        return WhenType.MatchesText;
    }
}
