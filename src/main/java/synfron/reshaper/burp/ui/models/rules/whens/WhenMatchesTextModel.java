package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class WhenMatchesTextModel extends WhenModel<WhenMatchesTextModel, WhenMatchesText> {

    @Getter
    private String sourceText = "";
    @Getter
    private String matchText = "";
    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier = "";
    @Getter
    private MessageValueType messageValueType;
    @Getter
    private String messageValuePath = "";
    @Getter
    private MatchType matchType;
    @Getter
    public boolean useMessageValue;

    public WhenMatchesTextModel(WhenMatchesText when, Boolean isNew) {
        super(when, isNew);
        sourceText = VariableString.getTag(when.getSourceText(), sourceText);
        matchText = VariableString.getTag(when.getMatchText(), matchText);
        messageValue = when.getMessageValue();
        identifier = VariableString.getTag(when.getIdentifier(), identifier);
        messageValueType = when.getMessageValueType();
        messageValuePath = VariableString.getTag(when.getMessageValuePath(), messageValuePath);
        matchType = when.getMatchType();
        useMessageValue = when.isUseMessageValue();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
        propertyChanged("sourceText", sourceText);
    }

    public void setMatchText(String matchText) {
        this.matchText = matchText;
        propertyChanged("matchText", matchText);
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
    }

    public void setMessageValueType(MessageValueType messageValueType) {
        this.messageValueType = messageValueType;
        propertyChanged("messageValueType", messageValueType);
    }

    public void setMessageValuePath(String messageValuePath) {
        this.messageValuePath = messageValuePath;
        propertyChanged("messageValuePath", messageValuePath);
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
        propertyChanged("matchType", matchType);
    }

    public void setUseMessageValue(boolean useMessageValue) {
        this.useMessageValue = useMessageValue;
        propertyChanged("useMessageValue", useMessageValue);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (useMessageValue && StringUtils.isEmpty(identifier) && messageValue.isIdentifierRequired()) {
            errors.add("Source Identifier is required");
        }
        return errors;
    }

    public boolean persist() {
        if (validate().size() != 0) {
            return false;
        }
        ruleOperation.setIdentifier(VariableString.getAsVariableString(identifier));
        ruleOperation.setSourceText(VariableString.getAsVariableString(sourceText));
        ruleOperation.setMatchText(VariableString.getAsVariableString(matchText));
        ruleOperation.setMessageValue(messageValue);
        ruleOperation.setMessageValueType(messageValueType);
        ruleOperation.setMessageValuePath(VariableString.getAsVariableString(messageValuePath));
        ruleOperation.setMatchType(matchType);
        ruleOperation.setUseMessageValue(useMessageValue);
        return super.persist();
    }

    @Override
    public boolean record() {
        if (validate().size() != 0) {
            return false;
        }
        setValidated(true);
        return true;
    }

    @Override
    public RuleOperationModelType<WhenMatchesTextModel, WhenMatchesText> getType() {
        return WhenModelType.MatchesText;
    }
}
