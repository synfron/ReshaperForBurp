package synfron.reshaper.burp.core.rules.thens.entities.script;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeObject;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.thens.ThenType;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.Serializer;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReshaperObj {
    public VariablesObj variables = new VariablesObj();
    public EventObj event = new EventObj();

    public static class VariablesObj {

        public String getGlobalVariable(String name) {
            return getVariable(Dispatcher.getCurrent().getEventInfo().getWorkspace().getGlobalVariables(), name);
        }

        public String getEventVariable(String name) {
            return getVariable(Dispatcher.getCurrent().getEventInfo().getVariables(), name);
        }

        public String getSessionVariable(String name) {
            return getVariable(Dispatcher.getCurrent().getEventInfo().getSessionVariables(), name);
        }

        public String[] getGlobalListVariable(String name) {
            Workspace workspace = Dispatcher.getCurrent().getEventInfo().getWorkspace();
            return getListVariable(workspace.getGlobalVariables(), name);
        }

        public String[] getEventListVariable(String name) {
            return getListVariable(Dispatcher.getCurrent().getEventInfo().getVariables(), name);
        }

        public String[] getSessionListVariable(String name) {
            return getListVariable(Dispatcher.getCurrent().getEventInfo().getSessionVariables(), name);
        }

        private String getVariable(Variables variables, String name) {
            if (variables != null) {
                Variable variable = variables.getOrDefault(Variables.asKey(name, false));
                return variable != null ?
                        TextUtils.toString(variable.getValue()) :
                        null;
            }
            return null;
        }

        private String[] getListVariable(Variables variables, String name) {
            if (variables != null) {
                ListVariable variable = (ListVariable) variables.getOrDefault(Variables.asKey(name, true));
                return variable != null ?
                        variable.getValues().stream().map(TextUtils::toString).toArray(String[]::new) :
                        null;
            }
            return null;
        }

        public void setGlobalVariable(String name, String value) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            Dispatcher.getCurrent().getEventInfo().getWorkspace().getGlobalVariables().add(Variables.asKey(name, false)).setValue(value);
        }

        public void setEventVariable(String name, String value) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            Dispatcher.getCurrent().getEventInfo().getVariables().add(Variables.asKey(name, false)).setValue(value);
        }

        public void setSessionVariable(String name, String value) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            Dispatcher.getCurrent().getEventInfo().getSessionVariables().add(Variables.asKey(name, false)).setValue(value);
        }

        public void setGlobalListVariable(String name, Object[] values, String delimiter) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            ListVariable variable = (ListVariable) Dispatcher.getCurrent().getEventInfo().getWorkspace().getGlobalVariables().add(Variables.asKey(name, true));
            variable.setValues(values, delimiter, SetListItemsPlacement.Overwrite);
        }

        public void setEventListVariable(String name, Object[] values, String delimiter) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            ListVariable variable = (ListVariable) Dispatcher.getCurrent().getEventInfo().getVariables().add(Variables.asKey(name, true));
            variable.setValues(values, delimiter, SetListItemsPlacement.Overwrite);
        }

        public void setSessionListVariable(String name, Object[] values, String delimiter) {
            if (!VariableString.isValidVariableName(name)) {
                throw new IllegalArgumentException(String.format("Invalid variable name '%s'", name));
            }
            ListVariable variable = (ListVariable) Dispatcher.getCurrent().getEventInfo().getSessionVariables().add(Variables.asKey(name, true));
            variable.setValues(values, delimiter, SetListItemsPlacement.Overwrite);
        }

        public void deleteGlobalVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getWorkspace().getGlobalVariables().remove(Variables.asKey(name, false));
        }

        public void deleteEventVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getVariables().remove(Variables.asKey(name, false));
        }

        public void deleteSessionVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getSessionVariables().remove(Variables.asKey(name, false));
        }

        public void deleteGlobalListVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getWorkspace().getGlobalVariables().remove(Variables.asKey(name, true));
        }

        public void deleteEventListVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getVariables().remove(Variables.asKey(name, true));
        }

        public void deleteSessionListVariable(String name) {
            Dispatcher.getCurrent().getEventInfo().getSessionVariables().remove(Variables.asKey(name, true));
        }
    }

    public static class EventObj {

        public List<String> getMessageValueKeys() {
            EventInfo eventInfo = (EventInfo)Dispatcher.getCurrent().getDataBag().get("eventInfo");
            return Arrays.stream(MessageValue.values())
                    .filter(value -> value.hasProtocolType(eventInfo.getProtocolType()))
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }

        public String getMessageValue(String key, String identifier) {
            EventInfo eventInfo = Dispatcher.getCurrent().getEventInfo();
            MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, key);
            if (messageValue == null || !messageValue.hasProtocolType(eventInfo.getProtocolType())) {
                throw new IllegalArgumentException(String.format("Invalid message value key: '%s'", key));
            }
            return MessageValueHandler.getValue(
                    eventInfo,
                    EnumUtils.getEnumIgnoreCase(MessageValue.class, key),
                    VariableString.getAsVariableString(identifier, false),
                    GetItemPlacement.Last
            );
        }

        public void setMessageValue(String key, String identifier, String value) {
            EventInfo eventInfo = Dispatcher.getCurrent().getEventInfo();
            MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, key);
            if (messageValue == null || !messageValue.hasProtocolType(eventInfo.getProtocolType())) {
                throw new IllegalArgumentException(String.format("Invalid message value key: '%s'", key));
            }
            MessageValueHandler.setValue(
                    eventInfo,
                    EnumUtils.getEnumIgnoreCase(MessageValue.class, key),
                    VariableString.getAsVariableString(identifier, false),
                    SetItemPlacement.Only,
                    value
            );
        }

        public String runThen(String thenType, NativeObject thenData) {
            Dispatcher dispatcher = Dispatcher.getCurrent();
            EventInfo eventInfo = dispatcher.getEventInfo();
            Workspaces.get().setCurrentWorkspace(eventInfo.getWorkspace());
            String adjustedThenTypeName = StringUtils.prependIfMissing(thenType, "Then");
            Stream<ThenType<?>> supportedThenTypes = Stream.of(
                    ThenType.BuildHttpMessage,
                    ThenType.Comment,
                    ThenType.DeleteValue,
                    ThenType.DeleteVariable,
                    ThenType.Drop,
                    ThenType.Evaluate,
                    ThenType.Extract,
                    ThenType.Generate,
                    ThenType.Highlight,
                    ThenType.Intercept,
                    ThenType.Log,
                    ThenType.ParseHttpMessage,
                    ThenType.ReadFile,
                    ThenType.SaveFile,
                    ThenType.SendMessage,
                    ThenType.SendRequest,
                    ThenType.SendTo,
                    ThenType.SetEncoding,
                    ThenType.SetEventDirection,
                    ThenType.SetValue,
                    ThenType.SetVariable,
                    ThenType.Transform
            );
            Class<?> thenClass = supportedThenTypes
                    .filter(type -> eventInfo.getProtocolType().accepts(ProtocolType.fromRuleOperationType(type.getType())))
                    .filter(type -> type.getType().getSimpleName().equalsIgnoreCase(adjustedThenTypeName))
                    .map(RuleOperationType::getType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Then type '%s' is not available", thenType)));
            thenData.defineProperty("@class", "." + thenClass.getSimpleName(), 0);
            String thenDataJson = NativeJSON.stringify(
                    dispatcher.getContext(),
                    dispatcher.getContext().initSafeStandardObjects(),
                    thenData,
                    null,
                    null
            ).toString();
            Then<?> then = (Then<?>)Serializer.deserialize(thenDataJson, thenClass);
            return then.perform(eventInfo).toString();
        }

        public void setRuleResponse(String ruleResponse) {
            switch (ruleResponse.toUpperCase()) {
                case "CONTINUE":
                    Dispatcher.getCurrent().getDataBag().put("ruleResponse", RuleResponse.Continue);
                    break;
                case "BREAKTHENS":
                    Dispatcher.getCurrent().getDataBag().put("ruleResponse", RuleResponse.BreakThens);
                    break;
                case "BREAKRULES":
                    Dispatcher.getCurrent().getDataBag().put("ruleResponse", RuleResponse.BreakRules);
                    break;
            }
        }
    }
}
