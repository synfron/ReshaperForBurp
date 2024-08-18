package synfron.reshaper.burp.ui.models.rules.wizard.matchreplace;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.rules.thens.ThenSetValue;
import synfron.reshaper.burp.core.rules.whens.WhenEventDirection;
import synfron.reshaper.burp.core.rules.whens.WhenMatchesText;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.ui.models.rules.RuleModel;
import synfron.reshaper.burp.ui.models.rules.thens.ThenSetValueModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenEventDirectionModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenMatchesTextModel;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModel;
import synfron.reshaper.burp.ui.utils.IPrompterModel;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class MatchAndReplaceWizardModel implements IPrompterModel<MatchAndReplaceWizardModel> {
    private final RuleModel rule;

    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    private boolean invalidated;
    private boolean dismissed;
    private MatchType matchType = MatchType.Url;
    private String identifier = "";
    private String match = "";
    private String replace = "";
    private boolean regexMatch;


    @Setter
    private ModalPrompter<MatchAndReplaceWizardModel> modalPrompter;

    public MatchAndReplaceWizardModel(RuleModel rule) {
        this.rule = rule;
    }

    public void resetPropertyChangedListener() {
        propertyChangedEvent.clearListeners();
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
        propertyChanged("matchType", matchType);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        propertyChanged("identifier", identifier);
    }

    public void setMatch(String match) {
        this.match = match;
        propertyChanged("match", match);
    }

    public void setReplace(String replace) {
        this.replace = replace;
        propertyChanged("replace", replace);
    }

    public void setRegexMatch(boolean regexMatch) {
        this.regexMatch = regexMatch;
        propertyChanged("regexMatch", regexMatch);
    }

    public MatchAndReplaceWizardModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (!matchType.isAllowEmptyMatch() && StringUtils.isEmpty(match)) {
            errors.add("Match is required");
        }
        return errors;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
        propertyChanged("invalidated", invalidated);
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        propertyChanged("dismissed", dismissed);
    }

    public boolean updateRule() {
        if (validate().isEmpty()) {
            boolean useContainsMatch = !regexMatch && !matchType.isRequiresRegex();
            String whenMatchText = regexMatch || useContainsMatch ? match : Pattern.quote(match);
            String thenMatchText = regexMatch ? match : Pattern.quote(match);
            String thenReplace = replace;
            MessageValue messageValue = matchType.getMessageValue();

            if (!match.isEmpty()) {
                switch (matchType) {
                    case RequestHeaderLine, ResponseHeaderLine -> {
                        whenMatchText = "(?m)" + whenMatchText;
                        thenMatchText = "(?m)" + thenMatchText;
                    }
                    case RequestParamName -> {
                        whenMatchText = "(?<=^|&)([^=]*?)" + whenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=&").replaceAll("(?<!\\\\)\\.", "[^=&]");
                        thenMatchText = "(?<=^|&)([^=]*?)" + thenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=&").replaceAll("(?<!\\\\)\\.", "[^=&]");
                        thenReplace = "$1" + replace;
                    }
                    case RequestParamValue -> {
                        whenMatchText = "(?<==)([^&]*?)" + whenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=&").replaceAll("(?<!\\\\)\\.", "[^=&]");
                        thenMatchText = "(?<==)([^&]*?)" + thenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=&").replaceAll("(?<!\\\\)\\.", "[^=&]");
                        thenReplace = "$1" + replace;
                    }
                    case RequestCookieName -> {
                        whenMatchText = "(?<=^|(;\\s?))([^=]*?)" + whenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=;").replaceAll("(?<!\\\\)\\.", "[^=;]");
                        thenMatchText = "(?<=^|(;\\s?))([^=]*?)" + thenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=;").replaceAll("(?<!\\\\)\\.", "[^=;]");
                        thenReplace = "$1" + replace;
                    }
                    case RequestCookieValue -> {
                        whenMatchText = "(?<==)([^;]*?)" + whenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=;").replaceAll("(?<!\\\\)\\.", "[^=;]");
                        thenMatchText = "(?<==)([^;]*?)" + thenMatchText.replaceAll("(?<!\\\\)\\[\\^", "[^=;").replaceAll("(?<!\\\\)\\.", "[^=;]");
                        thenReplace = "$1" + replace;
                    }
                }
            }

            addWhens(messageValue, useContainsMatch, whenMatchText);

            addThens(messageValue, thenMatchText, thenReplace);

            setInvalidated(false);
            return true;
        }
        setInvalidated(true);
        return false;
    }

    private void addThens(MessageValue messageValue, String thenMatchText, String thenReplace) {
        if (!match.isEmpty()) {
            ThenSetValue replaceValue = new ThenSetValue();
            replaceValue.setSourceMessageValue(messageValue);
            if (matchType.getMessageValue().isIdentifierRequired()) {
                replaceValue.setSourceIdentifier(matchType.hasCustomIdentifier() ?
                        VariableString.getAsVariableString(identifier, true) :
                        VariableString.getAsVariableString(matchType.getIdentifier(), false)
                );
            }
            replaceValue.setUseMessageValue(true);
            replaceValue.setDestinationMessageValue(messageValue);
            if (matchType.hasIdentifier()) {
                replaceValue.setDestinationIdentifier(matchType.hasCustomIdentifier() ?
                        VariableString.getAsVariableString(identifier, true) :
                        VariableString.getAsVariableString(matchType.getIdentifier(), false)
                );
            }
            replaceValue.setUseReplace(true);
            replaceValue.setRegexPattern(VariableString.getAsVariableString(thenMatchText, true));
            replaceValue.setReplacementText(VariableString.getAsVariableString(thenReplace, true));
            rule.addThen(new ThenSetValueModel(rule.getProtocolType(), replaceValue, true));

            switch (matchType) {
                case RequestHeaderLine, ResponseHeaderLine -> {
                    ThenSetValue removeExtraLines = new ThenSetValue();
                    removeExtraLines.setSourceMessageValue(messageValue);
                    removeExtraLines.setUseMessageValue(true);
                    removeExtraLines.setDestinationMessageValue(messageValue);
                    removeExtraLines.setUseReplace(true);
                    removeExtraLines.setRegexPattern(VariableString.getAsVariableString("(\\r\\n)+", true));
                    removeExtraLines.setReplacementText(VariableString.getAsVariableString("{{s:rn}}", true));
                    rule.addThen(new ThenSetValueModel(rule.getProtocolType(), removeExtraLines, true));
                }
            }
        } else {
            switch (matchType) {
                case RequestHeaderLine, ResponseHeaderLine -> {
                    ThenSetValue addHeader = new ThenSetValue();
                    addHeader.setText(VariableString.getAsVariableString(String.format("{{m:%s}}\r\n%s",
                            messageValue.name(),
                            thenReplace
                    ),true));
                    addHeader.setUseMessageValue(false);
                    addHeader.setDestinationMessageValue(messageValue);
                    rule.addThen(new ThenSetValueModel(rule.getProtocolType(), addHeader, true));
                }
            }
        }
    }

    private void addWhens(MessageValue messageValue, boolean useContainsMatch, String whenMatchText) {
        boolean directionSet = false;
        HttpDataDirection eventDirection = ObjectUtils.defaultIfNull(messageValue.getDataDirection(), HttpDataDirection.Request);
        for (int ruleIndex = rule.getWhens().size() - 1; ruleIndex >= 0; ruleIndex--) {
            WhenModel<?,?> whenModel = rule.getWhens().get(ruleIndex);
            if (whenModel instanceof WhenEventDirectionModel whenEventDirectionModel) {
                if (whenEventDirectionModel.getDataDirection() != eventDirection) {
                    whenEventDirectionModel.setDataDirection(eventDirection);
                }
                directionSet = true;
            }
        }
        if (!directionSet) {
            WhenEventDirection whenEventDirection = new WhenEventDirection();
            whenEventDirection.setDataDirection(eventDirection);
            rule.addWhen(new WhenEventDirectionModel(rule.getProtocolType(), whenEventDirection, true));
        }
        WhenMatchesText whenMatchesText = new WhenMatchesText();
        whenMatchesText.setMatchType(useContainsMatch ?
            synfron.reshaper.burp.core.rules.MatchType.Contains :
            synfron.reshaper.burp.core.rules.MatchType.Regex
        );
        whenMatchesText.setMatchText(VariableString.getAsVariableString(whenMatchText, true));
        whenMatchesText.setUseMessageValue(true);
        whenMatchesText.setMessageValue(matchType.getMessageValue());
        if (matchType.hasIdentifier()) {
            whenMatchesText.setIdentifier(matchType.hasCustomIdentifier() ?
                    VariableString.getAsVariableString(identifier, true) :
                    VariableString.getAsVariableString(matchType.getIdentifier(), false)
            );
        }

        rule.addWhen(new WhenMatchesTextModel(rule.getProtocolType(), whenMatchesText, true));
    }
}
