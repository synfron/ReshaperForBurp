package synfron.reshaper.burp.core.messages;

import burp.BurpExtender;
import ir.ac.iust.htmlchardet.HTMLCharsetDetector;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.CharsetUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Encoder {
    @Getter
    private static final String defaultEncoderName = "Default";
    @Getter
    private static final String autoDetectEncoderName = "Auto-detect";
    private Charset charset;
    @Getter
    private boolean useDefault;
    @Getter
    private boolean useAutoDetect;

    public Encoder(String encoding) {
        setEncoding(encoding);
    }

    public void setEncoding(String encoding) {
        useDefault = false;
        useAutoDetect = false;
        if (encoding == null || defaultEncoderName.equalsIgnoreCase(encoding)) {
            useDefault = true;
        } else if (autoDetectEncoderName.equalsIgnoreCase(encoding)) {
            useAutoDetect = true;
        } else {
            charset = Charset.forName(encoding);
        }
    }

    public static List<String> getEncodings() {
        return Stream.concat(
                Stream.of(defaultEncoderName, autoDetectEncoderName),
                Charset.availableCharsets().keySet().stream().sorted()
        ).collect(Collectors.toList());
    }

    public byte[] encode(String text) {
        byte[] encoded;
        if (useDefault) {
            encoded = defaultEncode(text);
        } else if (useAutoDetect) {
            encoded = charset != null ?
                    encode(text, charset, StandardCharsets.UTF_8) :
                    encode(text, StandardCharsets.UTF_8, null);
        } else {
            encoded = encode(text, charset, null);
        }
        return encoded;
    }

    public String decode(byte[] data) {
        String decoded;
        if (useDefault) {
            decoded = defaultDecode(data);
        } else if (useAutoDetect) {
            decoded = autoDetectDecode(data);
        } else {
            decoded = decode(data, charset);
        }
        return decoded;
    }

    private byte[] encode(String text, Charset primaryEncoding, Charset secondaryEncoding) {
        text = StringUtils.defaultString(text);
        try {
            return text.getBytes(primaryEncoding);
        } catch (Exception e) {
            return secondaryEncoding != null ? encode(text, secondaryEncoding, null) : defaultEncode(text);
        }
    }

    private String decode(byte[] data, Charset primaryEncoding) {
        data = ObjectUtils.defaultIfNull(data, new byte[0]);
        try {
            return new String(data, primaryEncoding);
        } catch (Exception e) {
            return defaultDecode(data);
        }
    }

    private byte[] defaultEncode(String text) {
        return BurpExtender.getCallbacks().getHelpers().stringToBytes(StringUtils.defaultString(text));
    }

    private String defaultDecode(byte[] data) {
        return BurpExtender.getCallbacks().getHelpers().bytesToString(ObjectUtils.defaultIfNull(data, new byte[0]));
    }

    private String autoDetectDecode(byte[] data) {
        try {
            charset = Charset.forName(new HTMLCharsetDetector().detect(data, true).replaceAll("_", "-"));
            return decode(data, charset);
        } catch (Exception e) {
            return defaultDecode(data);
        }
    }

    public static boolean isSupported(String encoding) {
        try {
            return StringUtils.isNotEmpty(encoding) && (
                    encoding.equalsIgnoreCase(defaultEncoderName) || encoding.equalsIgnoreCase(autoDetectEncoderName) || CharsetUtils.lookup(encoding) != null
            );
        } catch (Exception e) {
            return false;
        }
    }

}
