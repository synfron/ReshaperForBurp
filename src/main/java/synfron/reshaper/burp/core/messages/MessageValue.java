package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum MessageValue {
    SourceAddress("Source Address"),
    DestinationAddress("Destination Address"),
    DestinationPort("Destination Port"),
    HttpProtocol("Protocol"),
    HttpRequestMessage("Request Message"),
    HttpRequestStatusLine("Request Status Line"),
    HttpRequestMethod("Request Method"),
    HttpRequestUri("Request URI"),
    HttpRequestUriPath("Request URI Path"),
    HttpRequestUriQueryParameters("Request URI Query Parameters"),
    HttpRequestUriQueryParameter("Request URI Query Parameter"),
    HttpRequestHeaders("Request Headers"),
    HttpRequestHeader("Request Header"),
    HttpRequestCookie("Request Cookie"),
    HttpRequestBody("Request Body"),
    HttpResponseMessage("Response Message"),
    HttpResponseStatusLine("Response Status Line"),
    HttpResponseStatusCode("Response Status Code"),
    HttpResponseStatusMessage("Response Status Message"),
    HttpResponseHeaders("Response Headers"),
    HttpResponseHeader("Response Header"),
    HttpResponseCookie("Response Cookie"),
    HttpResponseBody("Response Body");

    @Getter
    private final String name;

    MessageValue(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
