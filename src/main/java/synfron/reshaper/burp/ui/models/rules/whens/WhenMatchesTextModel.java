package synfron.reshaper.burp.ui.models.rules.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.MatchType;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.Arrays;
import java.util.List;

public class WhenMatchesTextModel extends WhenModel<WhenMatchesTextModel, WhenMatchesText> {

    @Getter
    private String sourceText = "";
    @Getter
    private String matchText = "";
    @Getter
    private boolean ignoreCase;
    @Getter
    private MessageValue messageValue;
    @Getter
    private String identifier = "";
    @Getter
    private GetItemPlacement identifierPlacement;
    @Getter
    private MessageValueType messageValueType;
    @Getter
    private String messageValuePath = "";
    @Getter
    private MatchType matchType;
    @Getter
    public boolean useMessageValue;

    public WhenMatchesTextModel(ProtocolType protocolType, WhenMatchesText when, Boolean isNew) {
        super(protocolType, when, isNew);
        sourceText = VariableString.toString(when.getSourceText(), sourceText);
        ignoreCase = when.isIgnoreCase();
        matchText = VariableString.toString(when.getMatchText(), matchText);
        messageValue = when.getMessageValue() != null ? when.getMessageValue() : Arrays.stream(MessageValue.values()).filter(value -> value.isGettable(protocolType)).findFirst().orElse(null);
        identifier = VariableString.toString(when.getIdentifier(), identifier);
        identifierPlacement = when.getIdentifierPlacement();
        messageValueType = when.getMessageValueType();
        messageValuePath = VariableString.toString(when.getMessageValuePath(), messageValuePath);
        matchType = when.getMatchType();
        useMessageValue = when.isUseMessageValue();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    public void setIdentifierPlacement(GetItemPlacement identifierPlacement) {
        this.identifierPlacement = identifierPlacement;
        propertyChanged("identifierPlacement", identifierPlacement);
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
        propertyChanged("sourceText", sourceText);
    }

    public void setMatchText(String matchText) {
        this.matchText = matchText;
        propertyChanged("matchText", matchText);
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        propertyChanged("ignoreCase", ignoreCase);
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
        ruleOperation.setIdentifierPlacement(identifierPlacement);
        ruleOperation.setSourceText(VariableString.getAsVariableString(sourceText));
        ruleOperation.setMatchText(VariableString.getAsVariableString(matchText));
        ruleOperation.setIgnoreCase(ignoreCase);
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
    protected String getTargetName() {
        return abbreviateTargetName(matchText);
    }

    @Override
    public RuleOperationModelType<WhenMatchesTextModel, WhenMatchesText> getType() {
        return WhenModelType.MatchesText;
    }
}
