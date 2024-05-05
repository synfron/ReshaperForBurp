package synfron.reshaper.burp.core.vars;

import burp.BurpExtender;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VariableTag {

    private final static Pattern tagPattern;
    private final static Pattern paramPattern;

    static {
        String paramRegex = "(:(?<param>(\\\"(([^\\\"\\\\]+|\\\\.)+?)\\\")|([^:]+))?)";
        String subParamRegex = "(:(?<param>(\\\"(([^\\\"\\\\]+|\\\\.)+?)\\\")|([^:]+?))?)";
        String tagRegex = String.format("\\{\\{(?<name>%s)(?<params>%s*)\\}\\}", String.join("|", VariableSource.getSupportedNames()), subParamRegex);
        tagPattern = Pattern.compile(tagRegex);
        paramPattern = Pattern.compile(paramRegex);
    }

    public static List<String> parseParams(String params) {
        List<String> paramsList = new ArrayList<>();
        Matcher paramsMatcher = paramPattern.matcher(params);
        while (paramsMatcher.find()) {
            String param = StringUtils.defaultString(paramsMatcher.group("param"));
            if (param.startsWith("\"")) {
                param = TextUtils.jsonUnescape(TextUtils.stripQuotes(param));
            }
            paramsList.add(param);
        }
        return paramsList;
    }

    private static String joinParams(String[] params, boolean ignoreEmpty) {
        return Stream.of(params)
                .filter(param -> !ignoreEmpty || StringUtils.isNotEmpty(param))
                .map(StringUtils::defaultString)
                .map(param -> {
                    String value = TextUtils.jsonEscape(param);
                    return StringUtils.containsAny(param, '{', '}', '\"', ':') || !param.equals(value) ?
                            String.format("\"%s\"", value) :
                            param;
                })
                .collect(Collectors.joining(":"));
    }

    public static String getTag(VariableSource variableSource, String name, GetListItemPlacement itemPlacement, Integer index) {
        return getTag(
                variableSource,
                name,
                itemPlacement.toString(),
                itemPlacement.isHasIndexSetter() ? TextUtils.toString(index) : null
        );
    }

    public static String getTag(VariableSource variableSource, String name, SetListItemPlacement itemPlacement, Integer index) {
        return getTag(
                variableSource,
                name,
                itemPlacement != null ? itemPlacement.toString() : null,
                itemPlacement != null && itemPlacement.isHasIndexSetter() ? TextUtils.toString(index) : null
        );
    }

    public static String getTag(VariableSource variableSource, String... names) {
        return getTag(variableSource, true, names);
    }

    public static String getTag(VariableSource variableSource, boolean ignoreEmpty, String... names) {
        return String.format("{{%s:%s}}",
                StringUtils.defaultString(variableSource.name().toLowerCase()),
                joinParams(names, ignoreEmpty)
        );
    }

    public static String getShortTag(VariableSource variableSource, String... names) {
        return String.format("{{%s:%s}}",
                StringUtils.defaultString(variableSource.getShortName()),
                joinParams(names, true)
        );
    }

    public static String getShortTag(VariableSource variableSource, String name, GetListItemPlacement itemPlacement, Integer index) {
        return getShortTag(
                variableSource,
                name,
                itemPlacement != null ? itemPlacement.toString() : null,
                itemPlacement != null && itemPlacement.isHasIndexSetter() ? TextUtils.toString(index) : null
        );
    }

    public static List<Pair<Integer, Integer>> getVariableTagPositions(String str) {;
        return tagPattern.matcher(str).results()
                .map(result -> Pair.of(result.start(), result.end()))
                .collect(Collectors.toList());
    }


    private static String getSpecialChar(String sequences) {
        try {
            return TextUtils.parseSpecialChars(sequences);
        } catch (Exception e) {
            if (BurpExtender.getGeneralSettings().isEnableEventDiagnostics()) {
                Log.get().withMessage(String.format("Invalid use of special character variable tag: %s", VariableTag.getTag(VariableSource.Special, sequences))).withException(e).logErr();
            }
        }
        return null;
    }

    static String replaceTags(String str, List<VariableSourceEntry> variableSourceEntries, String replacement) {
        return tagPattern.matcher(str).replaceAll(match -> {
            String tag = match.group(0);
            String name = match.group("name");
            String params = match.group("params");
            VariableSource variableSource = VariableSource.get(name);
            List<String> paramsList = parseParams(params);
            if (variableSource == VariableSource.Special) {
                paramsList = List.of(StringUtils.defaultString(getSpecialChar(paramsList.getFirst())));
            }
            variableSourceEntries.add(new VariableSourceEntry(variableSource, paramsList, tag));
            return replacement;
        });
    }

    public static boolean hasTag(String text) {
        return !StringUtils.isEmpty(text) && tagPattern.matcher(text).find();
    }

    static String replaceTag(String str, String replacement) {
        return str.replaceAll(tagPattern.pattern(), replacement);
    }
}
