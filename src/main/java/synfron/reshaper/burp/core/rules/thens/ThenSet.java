package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

public abstract class ThenSet<T extends ThenSet<T>> extends Then<T> {
    protected final transient MessageValueHandler messageValueHandler = new MessageValueHandler();

    @Getter @Setter
    private boolean useMessageValue = true;
    @Getter @Setter
    private MessageValue sourceMessageValue = MessageValue.HttpRequestBody;
    @Getter @Setter
    private VariableString sourceIdentifier;
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
        return messageValueHandler.getValue(eventInfo, sourceMessageValue, sourceIdentifier);
    }

    protected String getReplacementValue(EventInfo eventInfo)
    {
        String text = "";
        if (useMessageValue)
        {
            text = getValue(eventInfo);
        } else {
            text = this.text.getText(eventInfo.getVariables());
        }
        if (sourceMessageValueType != MessageValueType.Text && sourceMessageValuePath != null)
        {
            switch (sourceMessageValueType)
            {
                case Json:
                    text = StringUtils.defaultString(TextUtils.getJsonValue(text, sourceMessageValuePath.getText(eventInfo.getVariables())));
                    break;
                case Html:
                    text = StringUtils.defaultString(TextUtils.getHtmlValue(text, sourceMessageValuePath.getText(eventInfo.getVariables())));
                    break;
            }
        }

        if (useReplace && replacementText != null)
        {
            text = text.replaceAll(regexPattern.getText(eventInfo.getVariables()), replacementText.getText(eventInfo.getVariables()));
        }
        return text;
    }
}
