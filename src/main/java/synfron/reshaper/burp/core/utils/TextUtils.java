package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class TextUtils {
    public static String getJsonValue(String text, String jsonPath) {
        return stripQuotes(JSONValue.toJSONString(JsonPath.parse(text).read(jsonPath)));
    }

    public static String setJsonValue(String text, String jsonPath, String value) {
        return stripQuotes(JsonPath.parse(text).set(jsonPath, JSONValue.parse(value)).jsonString());
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

    public static String bytesToString(byte[] bytes) {
        return BurpExtender.getCallbacks().getHelpers().bytesToString(ObjectUtils.defaultIfNull(bytes, new byte[0]));
    }

    public static byte[] stringToBytes(String value) {
        return BurpExtender.getCallbacks().getHelpers().stringToBytes(StringUtils.defaultString(value));
    }
}
