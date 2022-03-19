package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum MessageValue {
    SourceAddress("Source Address", null, true, false),
    DestinationAddress("Destination Address", DataDirection.Request, true, false),
    DestinationPort("Destination Port", DataDirection.Request, true, false),
    HttpProtocol("Protocol", DataDirection.Request, true, false),
    Url("URL", DataDirection.Request, true, false, true, false),
    HttpRequestMessage("Request Message", DataDirection.Request, true, true, false, false),
    HttpRequestStatusLine("Request Status Line", DataDirection.Request, false, false),
    HttpRequestMethod("Request Method", DataDirection.Request, false, false),
    HttpRequestUri("Request URI", DataDirection.Request, false, false),
    HttpRequestUriPath("Request URI Path", DataDirection.Request, false, false),
    HttpRequestUriQueryParameters("Request URI Query Parameters", DataDirection.Request, false, false),
    HttpRequestUriQueryParameter("Request URI Query Parameter", DataDirection.Request, false, true),
    HttpRequestHeaders("Request Headers", DataDirection.Request, false, false),
    HttpRequestHeader("Request Header", DataDirection.Request, false, true),
    HttpRequestCookie("Request Cookie", DataDirection.Request, false, true),
    HttpRequestBody("Request Body", DataDirection.Request, false, false),
    HttpResponseMessage("Response Message", DataDirection.Response, true, true, false, false),
    HttpResponseStatusLine("Response Status Line", DataDirection.Response, false, false),
    HttpResponseStatusCode("Response Status Code", DataDirection.Response, false, false),
    HttpResponseStatusMessage("Response Status Message", DataDirection.Response, false, false),
    HttpResponseHeaders("Response Headers", DataDirection.Response, false, false),
    HttpResponseHeader("Response Header", DataDirection.Response, false, true),
    HttpResponseCookie("Response Cookie", DataDirection.Response, false, true),
    HttpResponseBody("Response Body", DataDirection.Response, false, false);

    @Getter
    private final String name;
    @Getter
    private final DataDirection dataDirection;
    @Getter
    private final boolean topLevel;
    @Getter
    private final boolean messageGettable;
    @Getter
    private final boolean messageSettable;
    @Getter
    private final boolean identifierRequired;

    MessageValue(String name, DataDirection dataDirection, boolean topLevel, boolean identifierRequired) {
        this.name = name;
        this.dataDirection = dataDirection;
        this.topLevel = topLevel;
        this.messageGettable = !topLevel;
        this.messageSettable = !topLevel;
        this.identifierRequired = identifierRequired;
    }

    MessageValue(String name, DataDirection dataDirection, boolean topLevel, boolean messageGettable, boolean messageSettable, boolean identifierRequired) {
        this.name = name;
        this.dataDirection = dataDirection;
        this.topLevel = topLevel;
        this.messageGettable = messageGettable;
        this.messageSettable = messageSettable;
        this.identifierRequired = identifierRequired;
    }

    @Override
    public String toString() {
        return name;
    }
}
