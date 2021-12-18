package synfron.reshaper.burp.core.vars;

import burp.BurpExtender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.io.File;
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

    public String getTag()
    {
        return String.format(text, variables.stream().map(variable ->
                variable.getTag() != null ? variable.getTag() : VariableString.getTag(variable.getVariableSource(), variable.getName())
        ).toArray());
    }

    public static String getTag(VariableString variableString, String defaultValue) {
        return variableString != null ? variableString.getTag() : defaultValue;
    }

    public static String getTag(VariableSource variableSource, String variableName) {
        return String.format("{{%s:%s}}", variableSource.toString().toLowerCase(), variableName);
    }

    public static String getTag(MessageValue messageValue, String identifier) {
        return String.format(
                "{{message:%s%s}}",
                messageValue.name().toLowerCase(),
                messageValue.isIdentifierRequired() ? ":" + StringUtils.defaultString(identifier) : ""
        );
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
            Pattern pattern = Pattern.compile(String.format("\\{\\{(%s):(.+?)\\}\\}", String.join("|", VariableSource.getSupportedNames())));
            str = pattern.matcher(str).replaceAll(match -> {
                VariableSource variableSource = VariableSource.get(match.group(1));
                String entryName = variableSource == VariableSource.Special ?
                        getSpecialChar(match.group(2)) :
                        match.group(2);
                variableSourceEntries.add(
                        new VariableSourceEntry(VariableSource.get(match.group(1)), entryName, match.group(0))
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
            VariableSource variableSource = variable.getVariableSource();
            if (variableSource != null) {
                if (variableSource.isAccessor()) {
                    String value = switch (variable.getVariableSource()) {
                        case Message -> getMessageVariable(eventInfo, variable.getName());
                        case File -> getFileText(eventInfo, variable.getName());
                        case Special -> variable.getName();
                        default -> null;
                    };
                    variableVals.add(value);
                } else {
                    Variable value = switch (variable.getVariableSource()) {
                        case Global -> GlobalVariables.get().getOrDefault(variable.getName());
                        case Event -> eventInfo.getVariables().getOrDefault(variable.getName());
                        default -> null;
                    };
                    variableVals.add(value != null ? TextUtils.toString(value.getValue()) : null);
                }
            }
        }
        return String.format(text, variableVals.toArray());
    }

    private String getFileText(EventInfo eventInfo, String locator) {
        try {
            String[] variableNameParts = locator.split(":", 2);
            return FileUtils.readFileToString(new File(variableNameParts[1]), variableNameParts[0]);
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) {
                Log.get().withMessage(String.format("Error reading file with variable tag: %s", getTag(VariableSource.Special, locator))).withException(e).logErr();
            }
        }
        return null;
    }

    private static String getSpecialChar(String sequences) {
            try {
                return TextUtils.parseSpecialChars(sequences);
            } catch (Exception e) {
                if (BurpExtender.getGeneralSettings().isEnableEventDiagnostics()) {
                    Log.get().withMessage(String.format("Invalid use of special character variable tag: %s", getTag(VariableSource.Special, sequences))).withException(e).logErr();
                }
            }
            return null;
    }

    private String getMessageVariable(EventInfo eventInfo, String locator) {
        String[] variableNameParts = locator.split(":", 2);
        MessageValue messageValue = EnumUtils.getEnumIgnoreCase(MessageValue.class, CollectionUtils.elementAtOrDefault(variableNameParts, 0, ""));
        String identifier = CollectionUtils.elementAtOrDefault(variableNameParts, 1, "");
        if (messageValue != null) {
            String value = StringUtils.defaultString(
                    MessageValueHandler.getValue(eventInfo, messageValue, VariableString.getAsVariableString(identifier, false))
            );
            return value;
        }
        return null;
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
