package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtils {

    private static ParseContext jsonPathContext;

    private static ParseContext getJsonPathContext() {
        if (jsonPathContext == null) {
            jsonPathContext = JsonPath.using(Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL).addOptions(Option.SUPPRESS_EXCEPTIONS));
        }
        return jsonPathContext;
    }

    public static String getJsonValue(String text, String jsonPath) {
        return stripQuotes(JSONValue.toJSONString(getJsonPathContext().parse(text).read(jsonPath)));
    }

    public static String setJsonValue(String text, String jsonPath, String value) {
        return stripQuotes(getJsonPathContext().parse(text).set(jsonPath, JSONValue.parse(value)).jsonString());
    }

    private static String stripQuotes(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = StringUtils.removeStart(text, "\"");
            text = StringUtils.removeEnd(text, "\"");
        }
        return text;
    }

    public static String getHtmlValue(String text, String selector) {
        Document doc = Jsoup.parse(text);
        Elements elements = doc.select(selector);
        return elements.toString();
    }

    public static String setHtmlValue(String text, String selector, String value) {
        Document doc = Jsoup.parse(text);
        Elements elements = doc.select(selector);
        elements.before(value);
        elements.remove();
        return doc.toString();
    }

    public static String getParamValue(String text, String name) {
        return URLEncodedUtils.parse(text, StandardCharsets.UTF_8).stream()
                .filter(param -> param.getName().equals(name))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse(null);
    }

    public static String setParamValue(String text, String name, String value) {
        List<NameValuePair> params = new ArrayList<>();
        boolean found = false;
        for (NameValuePair param : URLEncodedUtils.parse(text, StandardCharsets.UTF_8)) {
            if (param.getName().equals(name)) {
                found = true;
                params.add(new BasicNameValuePair(name, value));
            } else {
                params.add(param);
            }
        }
        if (!found) {
            params.add(new BasicNameValuePair(name, value));
        }
        return URLEncodedUtils.format(params, StandardCharsets.UTF_8);
    }

    public static boolean isMatch(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).find();
    }

    public static boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    public static String bufferAppend(String text1, String text2, String suffix, int maxLength) {
        String newText;
        int untrimmedLength = text1.length() + text2.length();
        if (untrimmedLength > maxLength) {
            newText = text1.substring(untrimmedLength - maxLength) + text2 + suffix;
        } else {
            newText = text1 + text2 + suffix;
        }
        return newText;
    }

    public static String parseSpecialChars(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int charIndex = 0; charIndex < text.length(); charIndex++) {
            char chr = text.charAt(charIndex);
            switch (chr) {
                case 'n' -> stringBuilder.append('\n');
                case 't' -> stringBuilder.append('\t');
                case 'r' -> stringBuilder.append('\r');
                case 'b' -> stringBuilder.append('\b');
                case 'f' -> stringBuilder.append('\f');
                case 'x' -> {
                    stringBuilder.append((char)Integer.parseInt(text.substring(charIndex + 1, charIndex + 3), 16));
                    charIndex += 2;
                }
                case 'u' -> {
                    stringBuilder.append((char)Integer.parseInt(text.substring(charIndex + 1, charIndex + 5), 16));
                    charIndex += 4;
                }
                default -> stringBuilder.append(chr);
            }
        }
        return stringBuilder.toString();
    }
}
