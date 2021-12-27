package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;

public class WhenMatchesText extends When<WhenMatchesText> {
    @Getter
    @Setter
    private VariableString identifier;
    @Getter
    @Setter
    private GetItemPlacement identifierPlacement = GetItemPlacement.Last;
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
    public boolean isMatch(IEventInfo eventInfo) {
        boolean isMatch = false;
        String sourceText = null;
        String matchText = null;
        try {
            sourceText = useMessageValue ?
                    MessageValueHandler.getValue(eventInfo, messageValue, identifier, identifierPlacement) :
                    this.sourceText.getText(eventInfo);
            sourceText = getPathValue(sourceText, eventInfo);
            matchText = this.matchText.getText(eventInfo);

            isMatch = switch (matchType) {
                case BeginsWith -> sourceText.startsWith(matchText);
                case EndsWith -> sourceText.endsWith(matchText);
                case Contains -> sourceText.contains(matchText);
                case Equals -> sourceText.equals(matchText);
                case Regex -> TextUtils.isMatch(sourceText, matchText);
            };
        } catch (Exception ignored) {
        }
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logCompare(
                this, useMessageValue ? Arrays.asList(
                        Pair.of("messageValue", messageValue),
                        Pair.of("identifier", messageValue.isIdentifierRequired() ? VariableString.getTextOrDefault(eventInfo, identifier, null) : null),
                        Pair.of("identifierPlacement", messageValue.isIdentifierRequired() ? identifierPlacement : null)
                ) : null, matchType, matchText, sourceText, isMatch
        );
        return isMatch;
    }

    private String getPathValue(String value, IEventInfo eventInfo) {
        if (messageValueType != MessageValueType.Text && messageValuePath != null)
        {
            switch (messageValueType) {
                case Json -> value = TextUtils.getJsonValue(value, messageValuePath.getText(eventInfo));
                case Html -> value = TextUtils.getHtmlValue(value, messageValuePath.getText(eventInfo));
            }
        }
        return value;
    }

    @Override
    public RuleOperationType<WhenMatchesText> getType() {
        return WhenType.MatchesText;
    }
}
