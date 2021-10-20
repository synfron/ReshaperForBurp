package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum MessageValue {
    SourceAddress("Source Address", null),
    DestinationAddress("Destination Address", null),
    DestinationPort("Destination Port", null),
    HttpProtocol("Protocol", null),
    Url("URL", null),
    HttpRequestMessage("Request Message", DataDirection.Request),
    HttpRequestStatusLine("Request Status Line", DataDirection.Request),
    HttpRequestMethod("Request Method", DataDirection.Request),
    HttpRequestUri("Request URI", DataDirection.Request),
    HttpRequestUriPath("Request URI Path", DataDirection.Request),
    HttpRequestUriQueryParameters("Request URI Query Parameters", DataDirection.Request),
    HttpRequestUriQueryParameter("Request URI Query Parameter", DataDirection.Request),
    HttpRequestHeaders("Request Headers", DataDirection.Request),
    HttpRequestHeader("Request Header", DataDirection.Request),
    HttpRequestCookie("Request Cookie", DataDirection.Request),
    HttpRequestBody("Request Body", DataDirection.Request),
    HttpResponseMessage("Response Message", DataDirection.Response),
    HttpResponseStatusLine("Response Status Line", DataDirection.Response),
    HttpResponseStatusCode("Response Status Code", DataDirection.Response),
    HttpResponseStatusMessage("Response Status Message", DataDirection.Response),
    HttpResponseHeaders("Response Headers", DataDirection.Response),
    HttpResponseHeader("Response Header", DataDirection.Response),
    HttpResponseCookie("Response Cookie", DataDirection.Response),
    HttpResponseBody("Response Body", DataDirection.Response);

    @Getter
    private final String name;
    @Getter
    private final DataDirection dataDirection;

    MessageValue(String name, DataDirection dataDirection) {
        this.name = name;
        this.dataDirection = dataDirection;
    }

    @Override
    public String toString() {
        return name;
    }
}
