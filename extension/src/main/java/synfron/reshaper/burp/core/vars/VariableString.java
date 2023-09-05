package synfron.reshaper.burp.core.vars;

import burp.BurpExtender;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.getters.VariableGetterProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static List<Pair<Integer, Integer>> getVariableTagPositions(String str) {
        Pattern pattern = Pattern.compile(String.format("\\{\\{(%s):(.+?)\\}\\}", String.join("|", VariableSource.getSupportedNames())));
        return pattern.matcher(str).results()
                .map(result -> Pair.of(result.start(), result.end()))
                .collect(Collectors.toList());
    }

    public static VariableString getAsVariableString(String str, boolean requiresParsing) {
        if (str == null) {
            return null;
        }
        str = str.replace("%", "%%");
        if (requiresParsing) {
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
        } else {
            return new VariableString(str, Collections.emptyList());
        }
    }

    public Integer getInt(EventInfo eventInfo) {
        return TextUtils.asInt(getText(eventInfo));
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

    private static String getSpecialChar(String sequences) {
        try {
            return TextUtils.parseSpecialChars(sequences);
        } catch (Exception e) {
            if (BurpExtender.getGeneralSettings().isEnableEventDiagnostics()) {
                Log.get().withMessage(String.format("Invalid use of special character variable tag: %s", VariableSourceEntry.getTag(VariableSource.Special, sequences))).withException(e).logErr();
            }
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

    public static Integer getIntOrDefault(EventInfo eventInfo, VariableString variableString, Integer defaultValue) {
        Integer value;
        return variableString != null && !variableString.isEmpty() && (value = variableString.getInt(eventInfo)) != null ?
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
        String strippedText = formattedString.replaceAll(String.format("\\{\\{(%s):(.+?)\\}\\}", String.join("|", VariableSource.getSupportedNames())), "");
        return TextUtils.isInt(strippedText) || strippedText.isEmpty();
    }

    public static boolean hasTag(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        Pattern pattern = Pattern.compile(String.format("\\{\\{(%s):(.+?)\\}\\}", String.join("|", VariableSource.getSupportedNames())));
        return pattern.matcher(text).find();
    }
}
