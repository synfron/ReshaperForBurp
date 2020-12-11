package synfron.reshaper.burp.core.rules.thens.entities;

import org.apache.commons.lang3.EnumUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventObj {
    private final EventInfo eventInfo;
    private final MessageValueHandler messageValueHandler = new MessageValueHandler();

    public EventObj(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public List<String> getMessageValueKeys() {
        return Arrays.stream(MessageValue.values()).map(value -> value.name()).collect(Collectors.toList());
    }

    public String getMessageValue(String key, String identifier) {
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, key);
        if (messageValue == null) {
            throw new IllegalArgumentException(String.format("Invalid message value key: '%s'", key));
        }
        return messageValueHandler.getValue(
                eventInfo,
                EnumUtils.getEnumIgnoreCase(MessageValue.class, key),
                VariableString.getAsVariableString(identifier, false)
        );
    }

    public void setMessageValue(String key, String identifier, String value) {
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, key);
        if (messageValue == null) {
            throw new IllegalArgumentException(String.format("Invalid message value key: '%s'", key));
        }
        messageValueHandler.setValue(
                eventInfo,
                EnumUtils.getEnumIgnoreCase(MessageValue.class, key),
                VariableString.getAsVariableString(identifier, false),
                value
        );
    }
}
