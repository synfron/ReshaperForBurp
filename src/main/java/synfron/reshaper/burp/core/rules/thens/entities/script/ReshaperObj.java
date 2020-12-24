package synfron.reshaper.burp.core.rules.thens.entities.script;

import org.apache.commons.lang3.EnumUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.core.vars.VariableString;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReshaperObj {
    public VariablesObj variables = new VariablesObj();
    public EventObj event = new EventObj();

    public static class VariablesObj {

        public String getGlobalVariable(String name) {
            return getVariable(GlobalVariables.get(), name);
        }

        public String getEventVariable(String name) {
            return getVariable(((EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo")).getVariables(), name);
        }

        private String getVariable(Variables variables, String name) {
            Variable variable = variables.getOrDefault(name);
            return variable != null ?
                    TextUtils.toString(variable.getValue()) :
                    null;
        }

        public void setGlobalVariable(String name, String value) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            GlobalVariables.get().add(name).setValue(value);
        }

        public void setEventVariable(String name, String value) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            ((EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo")).getVariables().add(name).setValue(value);
        }

        public void deleteGlobalVariable(String name) {
            GlobalVariables.get().remove(name);
        }

        public void deleteEventVariable(String name) {
            ((EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo")).getVariables().remove(name);
        }
    }

    public static class EventObj {
        private final MessageValueHandler messageValueHandler = new MessageValueHandler();

        public List<String> getMessageValueKeys() {
            return Arrays.stream(MessageValue.values()).map(value -> value.name()).collect(Collectors.toList());
        }

        public String getMessageValue(String key, String identifier) {
            MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, key);
            if (messageValue == null) {
                throw new IllegalArgumentException(String.format("Invalid message value key: '%s'", key));
            }
            return messageValueHandler.getValue(
                    (EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo"),
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
                    (EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo"),
                    EnumUtils.getEnumIgnoreCase(MessageValue.class, key),
                    VariableString.getAsVariableString(identifier, false),
                    value
            );
        }
    }
}
