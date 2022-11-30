package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

public abstract class ThenSet<T extends ThenSet<T>> extends Then<T> {
    @Getter @Setter
    private boolean useMessageValue = true;
    @Getter @Setter
    private MessageValue sourceMessageValue;
    @Getter @Setter
    private VariableString sourceIdentifier;
    @Getter @Setter
    private GetItemPlacement sourceIdentifierPlacement = GetItemPlacement.Last;
    @Getter @Setter
    private MessageValueType sourceMessageValueType = MessageValueType.Text;
    @Getter @Setter
    private VariableString sourceMessageValuePath;
    @Getter @Setter
    private boolean useReplace;
    @Getter @Setter
    private VariableString regexPattern;
    @Getter @Setter
    private VariableString text;
    @Getter @Setter
    private VariableString replacementText;
    @Getter @Setter
    protected MessageValueType destinationMessageValueType = MessageValueType.Text;
    @Getter @Setter
    protected VariableString destinationMessageValuePath;

    private String getValue(EventInfo eventInfo)
    {
        return MessageValueHandler.getValue(eventInfo, sourceMessageValue, sourceIdentifier, sourceIdentifierPlacement);
    }

    protected String getReplacementValue(EventInfo eventInfo)
    {
        String text = "";
        if (useMessageValue)
        {
            text = getValue(eventInfo);
        } else {
            text = this.text.getText(eventInfo);
        }
        if (sourceMessageValueType != MessageValueType.Text && sourceMessageValuePath != null)
        {
            text = switch (sourceMessageValueType) {
                case Json -> StringUtils.defaultString(TextUtils.getJsonValue(text, sourceMessageValuePath.getText(eventInfo)));
                case Html -> StringUtils.defaultString(TextUtils.getHtmlValue(text, sourceMessageValuePath.getText(eventInfo)));
                case Params -> StringUtils.defaultString(TextUtils.getParamValue(text, sourceMessageValuePath.getText(eventInfo)));
                default -> text;
            };
        }

        if (useReplace && replacementText != null)
        {
            text = text.replaceAll(regexPattern.getText(eventInfo), replacementText.getText(eventInfo));
        }
        return text;
    }
}
