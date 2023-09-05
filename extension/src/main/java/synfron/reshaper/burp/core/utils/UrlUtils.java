package synfron.reshaper.burp.core.utils;

public class UrlUtils {

    public static Url getUrl(String protocol, String host, Integer port, String path) {
        return new Url(
                protocol,
                host,
                port == null ? Protocol.get(protocol).getDefaultPort(-1) : port,
                path
        );
    }
}
