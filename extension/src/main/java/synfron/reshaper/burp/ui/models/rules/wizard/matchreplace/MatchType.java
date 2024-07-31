package synfron.reshaper.burp.ui.models.rules.wizard.matchreplace;

import lombok.Getter;
import synfron.reshaper.burp.core.messages.MessageValue;

@Getter
public enum MatchType {
    Url("URL", MessageValue.Url, false, false),
    RequestFirstLine("Request First Line", MessageValue.HttpRequestStatusLine, false, false),
    RequestHeaderLine("Request Header Line", MessageValue.HttpRequestHeaders, true, true),
    RequestParamName("Request Param Name", MessageValue.HttpRequestUriQueryParameters, false, true),
    RequestParamValue("Request Param Value", MessageValue.HttpRequestUriQueryParameters, false, true),
    RequestParamValueByName("Request Param Value By Name", MessageValue.HttpRequestUriQueryParameters, false, true),
    RequestHeaderValueByName("Request Header Value By Name", MessageValue.HttpRequestHeader, false, false),
    RequestCookieName("Request Cookie Name", MessageValue.HttpRequestHeader, "Cookie", false, true),
    RequestCookieValue("Request Cookie Value", MessageValue.HttpRequestHeader, "Cookie", false, true),
    RequestCookieValueByName("Request Cookie Value By Name", MessageValue.HttpRequestCookie, false, true),
    RequestBody("Request Body", MessageValue.HttpRequestBody, false, false),
    ResponseHeaderLine("Response Header Line", MessageValue.HttpResponseHeaders, true, true),
    ResponseHeaderValueByName("Response Header Value By Name", MessageValue.HttpResponseHeader, false, false),
    ResponseBody("Response Body", MessageValue.HttpResponseBody, false, false);

    private final String name;
    private final MessageValue messageValue;
    private final String identifier;
    private final boolean allowEmptyMatch;
    private final boolean requiresRegex;

    MatchType(String name, MessageValue messageValue, boolean allowEmptyMatch, boolean requiresRegex) {
        this(name, messageValue, null, allowEmptyMatch, requiresRegex);
    }

    MatchType(String name, MessageValue messageValue, String identifier, boolean allowEmptyMatch, boolean requiresRegex) {
        this.name = name;
        this.messageValue = messageValue;
        this.identifier = identifier;
        this.allowEmptyMatch = allowEmptyMatch;
        this.requiresRegex = requiresRegex;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasIdentifier() {
        return messageValue.isIdentifierRequired();
    }

    public boolean hasCustomIdentifier() {
        return messageValue.isIdentifierRequired() && identifier == null;
    }
}
