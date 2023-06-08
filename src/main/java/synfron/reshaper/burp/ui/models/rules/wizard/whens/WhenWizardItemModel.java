package synfron.reshaper.burp.ui.models.rules.wizard.whens;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.*;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.Select;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhenWizardItemModel {

    @Getter
    private MessageValue messageValue;
    @Getter
    private final EventInfo eventInfo;
    @Getter
    private Select<String> identifiers = new Select<>(Collections.emptyList(), null);
    @Getter
    private WhenWizardMatchType matchType = WhenWizardMatchType.Equals;
    @Getter
    private String text;
    @Getter
    protected boolean deleted;

    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();

    public WhenWizardItemModel(MessageValue messageValue, EventInfo eventInfo) {
        this.eventInfo = eventInfo;
        setMessageValue(messageValue);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public WhenWizardItemModel withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public void setMessageValue(MessageValue messageValue) {
        this.messageValue = messageValue;
        propertyChanged("messageValue", messageValue);
        resetText();
        resetIdentifiers();
    }

    private void resetText() {
        try {
            text = !hasLongTextValue(messageValue) ? MessageValueHandler.getValue(
                    eventInfo,
                    messageValue,
                    identifiers.getSelectedOption() == null ? null : VariableString.getAsVariableString(identifiers.getSelectedOption(), false),
                    GetItemPlacement.Last
            ) : "";
        } catch (Exception e) {
            text = "";
        }
        propertyChanged("text", text);
    }

    private boolean hasLongTextValue(MessageValue messageValue) {
        return switch (messageValue) {
            case HttpRequestBody, HttpRequestHeaders, HttpRequestMessage, HttpResponseBody, HttpResponseHeaders, HttpResponseMessage -> true;
            default -> false;
        };
    }

    private void resetIdentifiers() {
        try {
            identifiers = new Select<>(MessageValueHandler.getIdentifier(eventInfo, messageValue), null);
        } catch (Exception e) {
            identifiers = new Select<>(Collections.emptyList(), null);
        }
        propertyChanged("identifiers", identifiers);
    }

    public void setIdentifier(String identifier) {
        identifiers.setSelectedOption(identifier);
        propertyChanged("identifiers", identifiers);
        resetText();
    }

    public void setMatchType(WhenWizardMatchType matchType) {
        this.matchType = matchType;
        propertyChanged("matchType", matchType);
    }

    public void setText(String text) {
        this.text = text;
        propertyChanged("text", text);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        propertyChanged("deleted", deleted);
    }

    public boolean requiresResponse() {
        return messageValue.getDataDirection() == HttpDataDirection.Response;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (messageValue.isIdentifierRequired() && (identifiers.getOptions().isEmpty() || StringUtils.isEmpty(identifiers.getSelectedOption()))) {
            errors.add("Has non-applicable constraint");
        }
        return errors;
    }
}
