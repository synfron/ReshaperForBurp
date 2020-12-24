package synfron.reshaper.burp.core.messages;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.List;

public class MessageValueHandler {
    public static boolean hasIdentifier(MessageValue messageValue) {
        return List.of(
                MessageValue.HttpRequestHeader,
                MessageValue.HttpResponseHeader,
                MessageValue.HttpRequestCookie,
                MessageValue.HttpResponseCookie,
                MessageValue.HttpRequestUriQueryParameter
        ).contains(messageValue);
    }

    public String getValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier)
    {
        String value = null;
        switch (messageValue)
        {
            case HttpRequestHeaders:
                value = eventInfo.getHttpRequestMessage().getHeaders().getText();
                break;
            case HttpResponseHeaders:
                value = eventInfo.getHttpResponseMessage().getHeaders().getText();
                break;
            case HttpRequestHeader:
                value = eventInfo.getHttpRequestMessage().getHeaders().getHeader(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpResponseHeader:
                value = eventInfo.getHttpResponseMessage().getHeaders().getHeader(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpRequestBody:
                value = eventInfo.getHttpRequestMessage().getBody().getText();
                break;
            case HttpResponseBody:
                value = eventInfo.getHttpResponseMessage().getBody().getText();
                break;
            case HttpRequestStatusLine:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getValue();
                break;
            case HttpResponseStatusLine:
                value = eventInfo.getHttpResponseMessage().getStatusLine().getValue();
                break;
            case HttpProtocol:
                value = eventInfo.getHttpProtocol();
                break;
            case HttpRequestCookie:
                value = eventInfo.getHttpRequestMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpResponseCookie:
                value = eventInfo.getHttpResponseMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo.getVariables()));
                break;
            case SourceAddress:
                value = eventInfo.getSourceAddress();
                break;
            case HttpRequestUri:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue();
                break;
            case HttpRequestMessage:
                value = eventInfo.getHttpRequestMessage().getText();
                break;
            case HttpResponseMessage:
                value = eventInfo.getHttpResponseMessage().getText();
                break;
            case DestinationPort:
                value = Integer.toString(eventInfo.getDestinationPort());
                break;
            case HttpRequestMethod:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getMethod();
                break;
            case DestinationAddress:
                value = eventInfo.getDestinationAddress();
                break;
            case HttpRequestUriPath:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getPath();
                break;
            case HttpResponseStatusCode:
                value = eventInfo.getHttpResponseMessage().getStatusLine().getCode();
                break;
            case HttpResponseStatusMessage:
                value = eventInfo.getHttpResponseMessage().getStatusLine().getMessage();
                break;
            case HttpRequestUriQueryParameter:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameter(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpRequestUriQueryParameters:
                value = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameters();
                break;
        }
        return StringUtils.defaultString(value);
    }

    public void setValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier, String replacementText) {
        switch (messageValue)
        {
            case HttpRequestHeaders:
                eventInfo.getHttpRequestMessage().setHeaders(StringUtils.defaultString(replacementText));
                break;
            case HttpResponseHeaders:
                eventInfo.getHttpResponseMessage().setHeaders(StringUtils.defaultString(replacementText));
                break;
            case HttpRequestHeader:
                eventInfo.getHttpRequestMessage().getHeaders().setHeader(identifier.getText(eventInfo.getVariables()), replacementText);
                break;
            case HttpResponseHeader:
                eventInfo.getHttpResponseMessage().getHeaders().setHeader(identifier.getText(eventInfo.getVariables()), replacementText);
                break;
            case HttpRequestBody:
                eventInfo.getHttpRequestMessage().setBody(StringUtils.defaultString(replacementText));
                break;
            case HttpResponseBody:
                eventInfo.getHttpResponseMessage().setBody(StringUtils.defaultString(replacementText));
                break;
            case HttpRequestStatusLine:
                eventInfo.getHttpRequestMessage().setStatusLine(replacementText);
                break;
            case HttpResponseStatusLine:
                eventInfo.getHttpResponseMessage().setStatusLine(replacementText);
                break;
            case HttpProtocol:
                eventInfo.setHttpProtocol(replacementText);
                break;
            case HttpRequestCookie:
                eventInfo.getHttpRequestMessage().getHeaders().getCookies().setCookie(identifier.getText(eventInfo.getVariables()), replacementText);
                break;
            case HttpResponseCookie:
                eventInfo.getHttpResponseMessage().getHeaders().getCookies().setCookie(identifier.getText(eventInfo.getVariables()), replacementText);
                break;
            case SourceAddress:
                throw new UnsupportedOperationException("Cannot set Source Address");
            case HttpRequestUri:
                eventInfo.getHttpRequestMessage().getStatusLine().setUrl(replacementText);
                break;
            case HttpRequestMessage:
                eventInfo.setHttpRequestMessage(TextUtils.stringToBytes(replacementText));
                break;
            case HttpResponseMessage:
                eventInfo.setHttpResponseMessage(TextUtils.stringToBytes(replacementText));
                break;
            case DestinationPort:
                eventInfo.setDestinationPort(Integer.parseInt(replacementText));
                break;
            case HttpRequestMethod:
                eventInfo.getHttpRequestMessage().getStatusLine().setMethod(replacementText);
                break;
            case DestinationAddress:
                eventInfo.setDestinationAddress(replacementText);
                break;
            case HttpRequestUriPath:
                eventInfo.getHttpRequestMessage().getStatusLine().getUrl().setPath(StringUtils.defaultString(replacementText));
                break;
            case HttpResponseStatusCode:
                eventInfo.getHttpResponseMessage().getStatusLine().setCode(replacementText);
                break;
            case HttpResponseStatusMessage:
                eventInfo.getHttpResponseMessage().getStatusLine().setMessage(StringUtils.defaultString(replacementText));
                break;
            case HttpRequestUriQueryParameter:
                eventInfo.getHttpRequestMessage().getStatusLine().getUrl().setQueryParameter(identifier.getText(eventInfo.getVariables()), replacementText);
                break;
            case HttpRequestUriQueryParameters:
                eventInfo.getHttpRequestMessage().getStatusLine().getUrl().setQueryParameters(StringUtils.defaultString(replacementText));
                break;
        }
    }
}
