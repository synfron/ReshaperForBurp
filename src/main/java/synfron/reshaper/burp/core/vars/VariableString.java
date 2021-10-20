package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VariableString implements Serializable {
    private final String text;
    private final List<VariableSourceEntry> variables;

    @JsonCreator
    public VariableString(@JsonProperty("text") String text, @JsonProperty("variables") List<VariableSourceEntry> variables) {
        this.text = text;
        this.variables = variables;
    }

    public static boolean isValidVariableName(String name) {
        return StringUtils.isNotEmpty(name) && !Pattern.matches("\\{\\{|}}", name);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(text);
    }

    public String getFormattedString()
    {
        return String.format(text, variables.stream().map(variable ->
                String.format("{{%s:%s}}", variable.getVariableSource().toString().toLowerCase(), variable.getName())
        ).toArray());
    }

    public static String getFormattedString(VariableString variableString, String defaultValue) {
        return variableString != null ? variableString.getFormattedString() : defaultValue;
    }
    
    public static VariableString getAsVariableString(String str) {
        return getAsVariableString(str, true);
    }

    public static VariableString getAsVariableString(String str, boolean requiresParsing)
    {
        if (str == null) {
            return null;
        }
        str = str.replace("%", "%%");
        if (requiresParsing)
        {
            List<VariableSourceEntry> variableSourceEntries = new ArrayList<>();
            Pattern pattern = Pattern.compile(String.format("\\{\\{(%s):(.+?)\\}\\}", Arrays.stream(VariableSource.values())
                    .map(value -> value.toString().toLowerCase())
                    .collect(Collectors.joining("|"))
            ));
            str = pattern.matcher(str).replaceAll(match -> {
                variableSourceEntries.add(
                        new VariableSourceEntry(EnumUtils.getEnumIgnoreCase(VariableSource.class, match.group(1)), match.group(2))
                );
                return "%s";
            });
            return new VariableString(str, variableSourceEntries);
        }
        else
        {
            return new VariableString(str, Collections.emptyList());
        }
    }

    public Integer getInt(EventInfo eventInfo)
    {
        String text = getText(eventInfo);
        Integer nullableValue = null;
        try {
            nullableValue = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {

        }
        return nullableValue;
    }

    public String getText(EventInfo eventInfo)
    {
        List<String> variableVals = new ArrayList<>();
        for (VariableSourceEntry variable : variables)
        {
            Variable value = switch (variable.getVariableSource()) {
                case Global -> GlobalVariables.get().getOrDefault(variable.getName());
                case Event -> eventInfo.getVariables().getOrDefault(variable.getName());
                case Message -> getMessageVariable(eventInfo, variable.getName());
                default -> null;
            };
            variableVals.add(value != null ? TextUtils.toString(value.getValue()) : null);
        }
        return String.format(text, variableVals.toArray());
    }

    private Variable getMessageVariable(EventInfo eventInfo, String variableName) {
        Variable variable = null;
        String[] variableNameParts = variableName.split(":", 2);
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, CollectionUtils.elementAtOrDefault(variableNameParts, 0, ""));
        String identifier = CollectionUtils.elementAtOrDefault(variableNameParts, 1, "");
        if (messageValue != null) {
            String value = StringUtils.defaultString(
                    MessageValueHandler.getValue(eventInfo, messageValue, VariableString.getAsVariableString(identifier, false))
            );
            variable = new Variable(messageValue.name());
            variable.setValue(value);
        }
        return variable;
    }

    public static String getTextOrDefault(EventInfo eventInfo, VariableString variableString, String defaultValue) {
        return variableString != null && !variableString.isEmpty() ?
                StringUtils.defaultIfEmpty(variableString.getText(eventInfo), defaultValue) :
                defaultValue;
    }

    public static String getText(EventInfo eventInfo, VariableString variableString) {
        return variableString != null ? variableString.getText(eventInfo) : null;
    }

    public static int getIntOrDefault(EventInfo eventInfo, VariableString variableString, int defaultValue) {
        return variableString != null && !variableString.isEmpty() ?
                variableString.getInt(eventInfo) :
                defaultValue;
    }

    public static boolean isPotentialInt(String formattedString) {
        if (StringUtils.isEmpty(formattedString)) {
            return false;
        }
        String strippedText = formattedString.replaceAll(String.format("\\{\\{(%s):(.+?)\\}\\}", Arrays.stream(VariableSource.values())
                .map(value -> value.toString().toLowerCase())
                .collect(Collectors.joining("|"))
        ), "");
        return TextUtils.isInt(strippedText);
    }
}
