package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;

public class UrlUtils {

    public static Url getUrl(String protocol, String host, Integer port, String path) {
        return new Url(
                protocol,
                host,
                port == null ? Protocol.get(protocol).getDefaultPort(-1) : port,
                path
        );
    }

    public static String urlEncode(String value) {
        return BurpExtender.getApi().utilities().urlUtils().encode(value);
    }

    public static String urlDecode(String value) {
        return BurpExtender.getApi().utilities().urlUtils().decode(value);
    }
}
