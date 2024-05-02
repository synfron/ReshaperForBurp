package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.utilities.Base64DecodingOptions;
import burp.api.montoya.utilities.DigestAlgorithm;
import com.jayway.jsonpath.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import synfron.reshaper.burp.core.messages.Encoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {

    private static ParseContext jsonPathContext;

    private static ParseContext getJsonPathContext() {
        if (jsonPathContext == null) {
            jsonPathContext = JsonPath.using(Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL).addOptions(Option.SUPPRESS_EXCEPTIONS));
        }
        return jsonPathContext;
    }

    public static String getJsonPathValue(String text, String jsonPath) {
        return stripQuotes(JSONValue.toJSONString(getJsonPathContext().parse(text).read(jsonPath)));
    }

    public static String setJsonPathValue(String text, String jsonPath, String value) {
        return stripQuotes(getJsonPathContext().parse(text).set(jsonPath, JSONValue.parse(value)).jsonString());
    }

    private static String stripQuotes(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = StringUtils.removeStart(text, "\"");
            text = StringUtils.removeEnd(text, "\"");
        }
        return text;
    }

    public static String getCssSelectorValue(String text, String selector) {
        Document doc = Jsoup.parse(text);
        boolean hasInnerHTML = selector.endsWith("::innerHTML");
        selector = hasInnerHTML ? StringUtils.removeEnd(selector, "::innerHTML") : selector;
        Elements elements = doc.select(selector);
        if (hasInnerHTML) {
            return elements.html();
        } else {
            return elements.toString();
        }
    }

    public static List<String> getJsonPathValues(String text, String jsonPath) {
        Object value = getJsonPathContext().parse(text).read(jsonPath);
        if (value instanceof JSONArray array) {
            return array.stream().map(item -> stripQuotes(JSONValue.toJSONString(item))).toList();
        }
        return List.of(stripQuotes(JSONValue.toJSONString(value)));
    }

    public static List<String> getCssSelectorValues(String text, String selector) {
        Document doc = Jsoup.parse(text);
        boolean hasInnerHTML = selector.endsWith("::innerHTML");
        selector = hasInnerHTML ? StringUtils.removeEnd(selector, "::innerHTML") : selector;
        Elements elements = doc.select(selector);
        if (hasInnerHTML) {
            return elements.stream().map(Element::html).toList();
        } else {
            return elements.stream().map(Element::toString).toList();
        }
    }

    public static List<String> getXPathValues(String text, String xpath) {
        Document doc = Jsoup.parse(text);
        List<Node> nodes = doc.selectXpath(xpath, Node.class);
        return nodes.stream().map(Node::toString).toList();
    }

    public static List<String> getRegexValues(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.results().map(MatchResult::group).toList();
    }

    public static String setCssSelectorValue(String text, String selector, String value) {
        Document doc = Jsoup.parse(text);
        boolean hasInnerHTML = selector.endsWith("::innerHTML");
        selector = hasInnerHTML ? StringUtils.removeEnd(selector, "::innerHTML") : selector;
        Elements elements = doc.select(selector);
        if (hasInnerHTML) {
            elements.html(value);
        } else {
            elements.before(value);
            elements.remove();
        }
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

    public static boolean isMatch(String text, String regex, boolean ignoreCase) {
        Pattern pattern = ignoreCase ?
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE) :
                Pattern.compile(regex);
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

    public static Integer asInt(String text) {
        Integer nullableValue = null;
        try {
            nullableValue = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {

        }
        return nullableValue;
    }

    public static Long asLong(String text) {
        Long nullableValue = null;
        try {
            nullableValue = Long.parseLong(text);
        } catch (NumberFormatException ignored) {

        }
        return nullableValue;
    }

    public static boolean isLong(String text) {
        try {
            Long.parseLong(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Double asDouble(String text) {
        Double nullableValue = null;
        try {
            nullableValue = Double.parseDouble(text);
        } catch (NumberFormatException ignored) {

        }
        return nullableValue;
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

    public static List<String> getChunks(String text, int size) {
        List<String> chunks = new ArrayList<>();
        for (int startIndex = 0; startIndex < size; startIndex += size) {
            chunks.add(text.substring(startIndex, Math.min(startIndex + size, text.length())));
        }
        return  chunks;
    }

    public static String changeCase(String text, PhraseCase phraseCase) {
        return switch (phraseCase) {
            case LowerCase -> text.toLowerCase();
            case UpperCase -> text.toUpperCase();
            case FlatCase -> splitPhrases(text).stream().map(token -> token.toString().toLowerCase()).collect(Collectors.joining());
            case PascalCase -> splitPhrases(text).stream().map(token -> uppercaseFirst(token.toString().toLowerCase())).collect(Collectors.joining());
            case CamelCase -> {
                AtomicBoolean isFirst = new AtomicBoolean(true);
                yield splitPhrases(text).stream().map(token -> {
                    if (!isFirst.get()) {
                        return uppercaseFirst(token.toString().toLowerCase());
                    } else {
                        isFirst.set(false);
                    }
                    return token.toString().toLowerCase();
                }).collect(Collectors.joining());
            }
            case SnakeCase -> splitPhrases(text).stream().map(token -> token.toString().toLowerCase()).collect(Collectors.joining("_"));
            case ConstantCase -> splitPhrases(text).stream().map(token -> token.toString().toUpperCase()).collect(Collectors.joining("_"));
            case DashCase -> splitPhrases(text).stream().map(token -> token.toString().toLowerCase()).collect(Collectors.joining("-"));
            case CobolCase -> splitPhrases(text).stream().map(token -> token.toString().toUpperCase()).collect(Collectors.joining("-"));
            case TitleCase -> splitPhrases(text).stream().map(token -> uppercaseFirst(token.toString().toLowerCase())).collect(Collectors.joining(" "));
            case SentenceCase -> uppercaseFirst(splitPhrases(text).stream().map(token -> token.toString().toLowerCase()).collect(Collectors.joining(" ")));
        };
    }

    private static List<StringBuilder> splitPhrases(String text) {
        if (text.isEmpty()) {
            return List.of(new StringBuilder());
        }
        char character = text.charAt(0);
        StringBuilder lastToken = new StringBuilder();
        lastToken.append(character);
        boolean isLastUppercase = Character.isUpperCase(character);
        boolean isLastDigit = Character.isDigit(character);
        boolean isLastLetter = Character.isLetter(character);
        boolean newToken = true;
        List<StringBuilder> tokens = new ArrayList<>();
        tokens.add(lastToken);
        for (int i = 1; i < text.length(); i++) {
            character = text.charAt(i);
            if (Character.isLetter(character)) {
                boolean isCurrentUppercase = Character.isUpperCase(character);
                if (!isLastLetter) {
                    newToken = true;
                    lastToken = new StringBuilder();
                    lastToken.append(character);
                    tokens.add(lastToken);
                } else if (isLastUppercase == isCurrentUppercase) {
                    lastToken.append(character);
                    newToken = false;
                } else {
                    if (newToken) {
                        lastToken.append(character);
                        newToken = false;
                    } else {
                        newToken = true;
                        lastToken = new StringBuilder();
                        lastToken.append(character);
                        tokens.add(lastToken);
                    }
                }
                isLastUppercase = isCurrentUppercase;
                isLastLetter = true;
                isLastDigit = false;
            } else if (Character.isDigit(character)) {
                if (isLastDigit) {
                    lastToken.append(character);
                    newToken = false;
                } else {
                    newToken = true;
                    lastToken = new StringBuilder();
                    lastToken.append(character);
                    tokens.add(lastToken);
                }
                isLastLetter = false;
                isLastDigit = true;
            } else {
                isLastLetter = false;
                isLastDigit = false;
            }
        }
        return tokens;
    }

    private static String uppercaseFirst(String text) {
        if (text.isEmpty()) {
            return text;
        } else if (text.length() == 1) {
            return text.toUpperCase();
        } else {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
    }

    public static String base64Decode(String value, Encoder encoder) {
        return encoder.decode(Base64.getDecoder().decode(value));
    }

    public static String base64Encode(String value, Encoder encoder) {
        return Base64.getEncoder().encodeToString(encoder.encode(value));
    }

    public static String base64UrlDecode(String value, Encoder encoder) {
        return encoder.decode(Base64.getUrlDecoder().decode(value));
    }

    public static String base64UrlEncode(String value, Encoder encoder) {
        return Base64.getUrlEncoder().encodeToString(encoder.encode(value));
    }

    public static String toHex(String value, Encoder encoder) {
        return HexFormat.of().formatHex(encoder.encode(value));
    }

    public static String fromHex(String value, Encoder encoder) {
        return encoder.decode(HexFormat.of().parseHex(value));
    }

    public static String htmlEncode(String value) {
        return BurpExtender.getApi().utilities().htmlUtils().encode(value);
    }

    public static String htmlDecode(String value) {
        return BurpExtender.getApi().utilities().htmlUtils().decode(value);
    }

    public static String xmlEncode(String value) {
        return StringEscapeUtils.escapeXml11(value);
    }

    public static String xmlDecode(String value) {
        return StringEscapeUtils.unescapeXml(value);
    }

    public static String jsonEscape(String value) {
        return StringEscapeUtils.escapeJson(value);
    }

    public static String jsonUnescape(String value) {
        return StringEscapeUtils.unescapeJson(value);
    }

    public static String sha1(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.SHA_1).getBytes());
    }

    public static String sha256(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.SHA_256).getBytes());
    }

    public static String sha512(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.SHA_512).getBytes());
    }

    public static String sha256V3(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.SHA3_256).getBytes());
    }

    public static String sha512V3(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.SHA3_512).getBytes());
    }

    public static String md5(String value) {
        return Hex.encodeHexString(BurpExtender.getApi().utilities().cryptoUtils().generateDigest(ByteArray.byteArray(value), DigestAlgorithm.MD5).getBytes());
    }

    public static String changeBase(String value, int sourceBase, int targetBase) {
        return Long.toString(Long.parseLong(value, sourceBase), targetBase);
    }
}
