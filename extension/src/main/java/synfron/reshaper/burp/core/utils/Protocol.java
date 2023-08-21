package synfron.reshaper.burp.core.utils;

import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;

public enum Protocol {
    Unknown(false, null, null, false),
    Http(false, 80, null, true),
    Https(true, 443, null, true),
    Ws(false, 80, Http, false),
    Wss(true, 443, Https, false);

    @Getter
    private final boolean secure;
    private final Integer defaultPort;
    private final Protocol backupProtocol;
    private final boolean javaSupported;

    Protocol(boolean secure, Integer defaultPort, Protocol backupProtocol, boolean javaSupported) {
        this.secure = secure;
        this.defaultPort = defaultPort;
        this.backupProtocol = backupProtocol;
        this.javaSupported = javaSupported;
    }

    public Integer getDefaultPort() {
        return defaultPort;
    }

    public int getDefaultPort(int defaultIfNone) {
        return defaultPort != null ? defaultPort : defaultIfNone;
    }

    public static String getJavaSupportProtocol(String protocolValue) {
        Protocol protocol = get(protocolValue);
        return protocol.javaSupported || protocol.backupProtocol == null ? protocolValue : protocol.backupProtocol.name().toLowerCase();
    }

    public static Protocol get(String protocol) {
        return EnumUtils.getEnumIgnoreCase(Protocol.class, protocol, Protocol.Unknown);
    }
}
