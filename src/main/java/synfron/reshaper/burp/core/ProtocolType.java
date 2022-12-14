package synfron.reshaper.burp.core;

import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;

public enum ProtocolType {
    Any,
    Http,
    WebSocket;

    public static ProtocolType fromRuleOperationType(Class<?> clazz) {
        boolean isHttp = IHttpRuleOperation.class.isAssignableFrom(clazz);
        boolean isWebSocket = IWebSocketRuleOperation.class.isAssignableFrom(clazz);
        if (isHttp && isWebSocket) {
            return Any;
        } else if (isHttp) {
            return Http;
        } else if (isWebSocket) {
            return WebSocket;
        }
        throw new IllegalArgumentException("No ProtocolType associated with class " + clazz);
    }

    public boolean accepts(ProtocolType protocolType) {
        return protocolType == ProtocolType.Any || this == ProtocolType.Any || this == protocolType;
    }
}
