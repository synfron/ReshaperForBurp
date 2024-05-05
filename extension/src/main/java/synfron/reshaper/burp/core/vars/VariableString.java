package synfron.reshaper.burp.core.vars;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.getters.VariableGetterProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class VariableString implements Serializable {
    private final String text;
    private final List<VariableSourceEntry> variables;

    private VariableString() {
        text = "";
        variables = Collections.emptyList();
    }

    public VariableString(String text, List<VariableSourceEntry> variables) {
        this.text = text;
        this.variables = variables;
    }

    public static boolean isValidVariableName(String name) {
        return StringUtils.isNotEmpty(name) && !Pattern.matches("\\{\\{|}}", name);
    }

    public boolean hasVariables() {
        return !variables.isEmpty();
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(text);
    }

    public static boolean isEmpty(VariableString variableString) {
        return variableString == null || variableString.isEmpty();
    }

    public String toString() {
        return String.format(text, variables.stream().map(VariableSourceEntry::getTag).toArray());
    }

    public static String toString(VariableString variableString, String defaultValue) {
        return variableString != null ? variableString.toString() : defaultValue;
    }

    public static VariableString getAsVariableString(String str) {
        return getAsVariableString(str, true);
    }

    public static VariableString getAsVariableString(String str, boolean requiresParsing) {
        if (str == null) {
            return null;
        }
        str = str.replace("%", "%%");
        if (requiresParsing) {
            List<VariableSourceEntry> variableSourceEntries = new ArrayList<>();
            str = VariableTag.replaceTags(str, variableSourceEntries, "%s");
            return new VariableString(str, variableSourceEntries);
        } else {
            return new VariableString(str, Collections.emptyList());
        }
    }

    public Integer getInt(EventInfo eventInfo) {
        return TextUtils.asInt(getText(eventInfo));
    }

    public Long getLong(EventInfo eventInfo) {
        return TextUtils.asLong(getText(eventInfo));
    }

    public Double getDouble(EventInfo eventInfo) {
        return TextUtils.asDouble(getText(eventInfo));
    }

    public String getText(EventInfo eventInfo) {
        List<String> variableVals = new ArrayList<>();
        for (VariableSourceEntry variable : variables) {
            VariableSource variableSource = variable.getVariableSource();
            if (variableSource != null) {
                variableVals.add(VariableGetterProvider.get(variableSource).getText(variable, eventInfo));
            }
        }
        return String.format(text, variableVals.toArray());
    }

    public static String getTextOrDefault(EventInfo eventInfo, VariableString variableString, String defaultValue) {
        return variableString != null && !variableString.isEmpty() ?
                StringUtils.defaultIfEmpty(variableString.getText(eventInfo), defaultValue) :
                defaultValue;
    }

    public static String getText(EventInfo eventInfo, VariableString variableString) {
        return variableString != null ? variableString.getText(eventInfo) : null;
    }

    public static Integer getIntOrDefault(EventInfo eventInfo, VariableString variableString, Integer defaultValue) {
        Integer value;
        return variableString != null && !variableString.isEmpty() && (value = variableString.getInt(eventInfo)) != null ?
                value :
                defaultValue;
    }

    public static Long getLongOrDefault(EventInfo eventInfo, VariableString variableString, Integer defaultValue) {
        Long value;
        return variableString != null && !variableString.isEmpty() && (value = variableString.getLong(eventInfo)) != null ?
                value :
                defaultValue;
    }

    public static Double getDoubleOrDefault(EventInfo eventInfo, VariableString variableString, Double defaultValue) {
        Double value;
        return variableString != null && !variableString.isEmpty() && (value = variableString.getDouble(eventInfo)) != null ?
                value :
                defaultValue;
    }

    public static boolean isPotentialInt(String formattedString) {
        if (StringUtils.isEmpty(formattedString)) {
            return false;
        }
        String strippedText = VariableTag.replaceTag(formattedString, "");
        return TextUtils.isInt(strippedText) || strippedText.isEmpty();
    }

    public static boolean isPotentialLong(String formattedString) {
        if (StringUtils.isEmpty(formattedString)) {
            return false;
        }

        String strippedText = VariableTag.replaceTag(formattedString, "");
        return TextUtils.isLong(strippedText) || strippedText.isEmpty();
    }

}
