package synfron.reshaper.burp.core.vars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
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
        return !Pattern.matches("\\{\\{|}}", name);
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

    public Integer getInt(Variables connectionVariables)
    {
        String text = getText(connectionVariables);
        Integer nullableValue = null;
        try {
            nullableValue = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {

        }
        return nullableValue;
    }

    public String getText(Variables connectionVariables)
    {
        List<String> variableVals = new ArrayList<>();
        for (VariableSourceEntry variable : variables)
        {
            Variable value = null;
            switch (variable.getVariableSource()) {
                case Global: value = GlobalVariables.get().getOrDefault(variable.getName()); break;
                case Event: value = connectionVariables != null ? connectionVariables.getOrDefault(variable.getName()) : null; break;
            }
            variableVals.add(Objects.toString(value));
        }
        return String.format(text, variableVals.toArray());
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
