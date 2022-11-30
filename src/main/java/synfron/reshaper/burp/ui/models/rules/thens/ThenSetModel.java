package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.thens.ThenSet;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;
import java.util.List;

public abstract class ThenSetModel<P extends ThenSetModel<P, T>, T extends ThenSet<T>> extends ThenModel<P, T> {

    @Getter
    protected boolean useMessageValue;
    @Getter
    protected MessageValue sourceMessageValue;
    @Getter
    protected MessageValueType sourceMessageValueType;
    @Getter
    private String sourceMessageValuePath = "";
    @Getter
    protected String sourceIdentifier = "";
    @Getter
    private GetItemPlacement sourceIdentifierPlacement;
    @Getter
    protected boolean useReplace;
    @Getter
    protected String regexPattern = "";
    @Getter
    protected String text = "";
    @Getter
    protected String replacementText = "";
    @Getter
    protected MessageValueType destinationMessageValueType;
    @Getter
    protected String destinationMessageValuePath = "";
    
    public ThenSetModel(ProtocolType protocolType, T then, Boolean isNew) {
        super(protocolType, then, isNew);
        useMessageValue = then.isUseMessageValue();
        sourceMessageValue = then.getSourceMessageValue() != null ? then.getSourceMessageValue() : Arrays.stream(MessageValue.values()).filter(value -> value.isGettable(protocolType)).findFirst().orElse(null);;
        sourceIdentifier = VariableString.toString(then.getSourceIdentifier(), sourceIdentifier);
        sourceIdentifierPlacement = then.getSourceIdentifierPlacement();
        sourceMessageValueType = then.getSourceMessageValueType();
        sourceMessageValuePath = VariableString.toString(then.getSourceMessageValuePath(), sourceMessageValuePath);
        useReplace = then.isUseReplace();
        regexPattern = VariableString.toString(then.getRegexPattern(), regexPattern);
        text = VariableString.toString(then.getText(), text);
        replacementText = VariableString.toString(then.getReplacementText(), replacementText);
        destinationMessageValueType = then.getDestinationMessageValueType();
        destinationMessageValuePath = VariableString.toString(then.getDestinationMessageValuePath(), destinationMessageValuePath);
    }

    public void setUseMessageValue(boolean useMessageValue) {
        this.useMessageValue = useMessageValue;
        propertyChanged("useMessageValue", useMessageValue);
    }

    public void setSourceMessageValue(MessageValue sourceMessageValue) {
        this.sourceMessageValue = sourceMessageValue;
        propertyChanged("sourceMessageValue", sourceMessageValue);
    }

    public void setSourceMessageValueType(MessageValueType sourceMessageValueType) {
        this.sourceMessageValueType = sourceMessageValueType;
        propertyChanged("sourceMessageValueType", sourceMessageValueType);
    }

    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
        propertyChanged("sourceIdentifier", sourceIdentifier);
    }

    public void setSourceIdentifierPlacement(GetItemPlacement sourceIdentifierPlacement) {
        this.sourceIdentifierPlacement = sourceIdentifierPlacement;
        propertyChanged("sourceIdentifierPlacement", sourceIdentifierPlacement);
    }

    public void setSourceMessageValuePath(String sourceMessageValuePath) {
        this.sourceMessageValuePath = sourceMessageValuePath;
        propertyChanged("sourceMessageValuePath", sourceMessageValuePath);
    }

    public void setUseReplace(boolean useReplace) {
        this.useReplace = useReplace;
        propertyChanged("useReplace", useReplace);
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
        propertyChanged("regexPattern", regexPattern);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setReplacementText(String replacementText) {
        this.replacementText = replacementText;
        propertyChanged("replacementText", replacementText);
    }

    public void setDestinationMessageValueType(MessageValueType destinationMessageValueType) {
        this.destinationMessageValueType = destinationMessageValueType;
        propertyChanged("destinationMessageValueType", destinationMessageValueType);
    }

    public void setDestinationMessageValuePath(String destinationMessageValuePath) {
        this.destinationMessageValuePath = destinationMessageValuePath;
        propertyChanged("destinationMessageValuePath", destinationMessageValuePath);
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (useMessageValue && StringUtils.isEmpty(sourceIdentifier) && sourceMessageValue.isIdentifierRequired()) {
            errors.add("Source Identifier is required");
        }
        return errors;
    }

    @Override
    public boolean persist() {
        ruleOperation.setUseMessageValue(useMessageValue);
        ruleOperation.setSourceMessageValue(sourceMessageValue);
        ruleOperation.setSourceMessageValueType(sourceMessageValueType);
        ruleOperation.setSourceIdentifier(VariableString.getAsVariableString(sourceIdentifier));
        ruleOperation.setSourceIdentifierPlacement(sourceIdentifierPlacement);
        ruleOperation.setSourceMessageValuePath(VariableString.getAsVariableString(sourceMessageValuePath));
        ruleOperation.setUseReplace(useReplace);
        ruleOperation.setRegexPattern(VariableString.getAsVariableString(regexPattern));
        ruleOperation.setText(VariableString.getAsVariableString(text));
        ruleOperation.setReplacementText(VariableString.getAsVariableString(replacementText));
        ruleOperation.setDestinationMessageValueType(destinationMessageValueType);
        ruleOperation.setDestinationMessageValuePath(VariableString.getAsVariableString(destinationMessageValuePath));

        setValidated(true);
        return true;
    }
}
