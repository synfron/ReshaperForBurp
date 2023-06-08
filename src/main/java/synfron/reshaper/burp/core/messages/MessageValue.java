package synfron.reshaper.burp.core.messages;

import lombok.Getter;
import synfron.reshaper.burp.core.ProtocolType;

public enum MessageValue {
    SourceAddress("HTTP Source Address", null, true, true, false, false, false, false, false),
    DestinationAddress("HTTP Destination Address", HttpDataDirection.Request, true, true, true, true, false, false, false),
    DestinationPort("HTTP Destination Port", HttpDataDirection.Request, true, true, true, true, false, false, false),
    HttpProtocol("URL Protocol", HttpDataDirection.Request, true, true, true, true, false, false, false),
    Url("URL", HttpDataDirection.Request, true, true, true, true, false, false, true, false, false),
    WebSocketMessage("WebSocket Message", null, true, false, false, true, true, false, false),
    HttpRequestMessage("Request Message", HttpDataDirection.Request, true, true, true, true, false, true, true, true, false),
    HttpRequestStatusLine("Request Status Line", HttpDataDirection.Request, false, true, true, true, false, false, false),
    HttpRequestMethod("Request Method", HttpDataDirection.Request, false, true, true, true, false, false, false),
    HttpRequestUri("Request URI", HttpDataDirection.Request, false, true, true, true, false, false, false),
    HttpRequestUriPath("Request URI Path", HttpDataDirection.Request, false, true, true, true, false, true, false),
    HttpRequestUriQueryParameters("Request URI Query Parameters", HttpDataDirection.Request, false, true, true, true, false, true, false),
    HttpRequestUriQueryParameter("Request URI Query Parameter", HttpDataDirection.Request, false, true, true, true, false, true, true),
    HttpRequestHeaders("Request Headers", HttpDataDirection.Request, false, true, true, true, false, true, false),
    HttpRequestHeader("Request Header", HttpDataDirection.Request, false, true, true, true, false, true, true),
    HttpRequestCookie("Request Cookie", HttpDataDirection.Request, false, true, true, true, false, true, true),
    HttpRequestBody("Request Body", HttpDataDirection.Request, false, true, true, true, false, true, false),
    HttpResponseMessage("Response Message", HttpDataDirection.Response, true, true, true, false, false, true, true, true, false),
    HttpResponseStatusLine("Response Status Line", HttpDataDirection.Response, false, true, true, false, false, false, false),
    HttpResponseStatusCode("Response Status Code", HttpDataDirection.Response, false, true, true, false, false, false, false),
    HttpResponseStatusMessage("Response Status Message", HttpDataDirection.Response, false, true, true, false, false, false, false),
    HttpResponseHeaders("Response Headers", HttpDataDirection.Response, false, true, true, false, false, true, false),
    HttpResponseHeader("Response Header", HttpDataDirection.Response, false, true, true, false, false, true, true),
    HttpResponseCookie("Response Cookie", HttpDataDirection.Response, false, true, true, false, false, true, true),
    HttpResponseBody("Response Body", HttpDataDirection.Response, false, true, true, false, false, true, false);

    @Getter
    private final String name;

    @Getter
    private final HttpDataDirection dataDirection;
    @Getter
    private final boolean topLevel;
    @Getter
    private final boolean httpGettable;
    @Getter
    private final boolean httpSettable;
    @Getter
    private final boolean webSocketGettable;
    @Getter
    private final boolean webSocketSettable;
    @Getter
    private final boolean innerLevelGettable;
    @Getter
    private final boolean innerLevelSettable;

    private final boolean deletable;
    @Getter
    private final boolean identifierRequired;

    MessageValue(String name, HttpDataDirection dataDirection, boolean topLevel, boolean httpGettable, boolean httpSettable, boolean webSocketGettable, boolean webSocketSettable, boolean deletable, boolean identifierRequired) {
        this.name = name;
        this.dataDirection = dataDirection;
        this.topLevel = topLevel;
        this.httpGettable = httpGettable;
        this.httpSettable = httpSettable;
        this.webSocketGettable = webSocketGettable;
        this.webSocketSettable = webSocketSettable;
        this.deletable = deletable;
        this.innerLevelGettable = !topLevel;
        this.innerLevelSettable = !topLevel;
        this.identifierRequired = identifierRequired;
    }

    MessageValue(String name, HttpDataDirection dataDirection, boolean topLevel, boolean httpGettable, boolean httpSettable, boolean webSocketGettable, boolean webSocketSettable, boolean deletable, boolean innerLevelGettable, boolean innerLevelSettable, boolean identifierRequired) {
        this.name = name;
        this.dataDirection = dataDirection;
        this.topLevel = topLevel;
        this.httpGettable = httpGettable;
        this.httpSettable = httpSettable;
        this.webSocketGettable = webSocketGettable;
        this.webSocketSettable = webSocketSettable;
        this.deletable = deletable;
        this.innerLevelGettable = innerLevelGettable;
        this.innerLevelSettable = innerLevelSettable;
        this.identifierRequired = identifierRequired;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasProtocolType(ProtocolType protocolType) {
        return switch (protocolType) {
            case Any -> httpGettable || httpSettable || webSocketGettable || webSocketSettable;
            case Http -> httpGettable || httpSettable;
            case WebSocket -> webSocketGettable || webSocketSettable;
        };
    }

    public boolean isDeletable(ProtocolType protocolType) {
        return isSettable(protocolType) && deletable;
    }

    public boolean isInnerLevelGettable(ProtocolType protocolType) {
        return isGettable(protocolType) && innerLevelGettable;
    }

    public boolean isInnerLevelSettable(ProtocolType protocolType) {
        return isSettable(protocolType) && innerLevelSettable;
    }

    public boolean isGettable(ProtocolType protocolType) {
        return switch (protocolType) {
            case Any -> httpGettable || webSocketGettable;
            case Http -> httpGettable;
            case WebSocket -> webSocketGettable;
        };
    }

    public boolean isSettable(ProtocolType protocolType) {
        return switch (protocolType) {
            case Any -> httpSettable || webSocketSettable;
            case Http -> httpSettable;
            case WebSocket -> webSocketSettable;
        };
    }
}
