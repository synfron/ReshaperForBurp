package synfron.reshaper.burp.ui.models.rules.thens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueType;
import synfron.reshaper.burp.core.rules.thens.ThenSet;
import synfron.reshaper.burp.core.vars.VariableString;

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
    
    public ThenSetModel(T then, Boolean isNew) {
        super(then, isNew);
        useMessageValue = then.isUseMessageValue();
        sourceMessageValue = then.getSourceMessageValue();
        sourceIdentifier = VariableString.getFormattedString(then.getSourceIdentifier(), sourceIdentifier);
        sourceMessageValueType = then.getSourceMessageValueType();
        sourceMessageValuePath = VariableString.getFormattedString(then.getSourceMessageValuePath(), sourceMessageValuePath);
        useReplace = then.isUseReplace();
        regexPattern = VariableString.getFormattedString(then.getRegexPattern(), regexPattern);
        text = VariableString.getFormattedString(then.getText(), text);
        replacementText = VariableString.getFormattedString(then.getReplacementText(), replacementText);
        destinationMessageValueType = then.getDestinationMessageValueType();
        destinationMessageValuePath = VariableString.getFormattedString(then.getDestinationMessageValuePath(), destinationMessageValuePath);
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
