package synfron.reshaper.burp.core.messages;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
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

    public static String getValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier)
    {
        String value = switch (messageValue) {
            case HttpRequestHeaders -> eventInfo.getHttpRequestMessage().getHeaders().getText();
            case HttpResponseHeaders -> eventInfo.getHttpResponseMessage().getHeaders().getText();
            case HttpRequestHeader -> eventInfo.getHttpRequestMessage().getHeaders().getHeader(identifier.getText(eventInfo));
            case HttpResponseHeader -> eventInfo.getHttpResponseMessage().getHeaders().getHeader(identifier.getText(eventInfo));
            case HttpRequestBody -> eventInfo.getHttpRequestMessage().getBody().getText();
            case HttpResponseBody -> eventInfo.getHttpResponseMessage().getBody().getText();
            case HttpRequestStatusLine -> eventInfo.getHttpRequestMessage().getStatusLine().getValue();
            case HttpResponseStatusLine -> eventInfo.getHttpResponseMessage().getStatusLine().getValue();
            case HttpProtocol -> eventInfo.getHttpProtocol();
            case Url -> eventInfo.getUrl();
            case HttpRequestCookie -> eventInfo.getHttpRequestMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo));
            case HttpResponseCookie -> eventInfo.getHttpResponseMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo));
            case SourceAddress -> eventInfo.getSourceAddress();
            case HttpRequestUri -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue();
            case HttpRequestMessage -> eventInfo.getHttpRequestMessage().getText();
            case HttpResponseMessage -> eventInfo.getHttpResponseMessage().getText();
            case DestinationPort -> Integer.toString(eventInfo.getDestinationPort());
            case HttpRequestMethod -> eventInfo.getHttpRequestMessage().getStatusLine().getMethod();
            case DestinationAddress -> eventInfo.getDestinationAddress();
            case HttpRequestUriPath -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getPath();
            case HttpResponseStatusCode -> eventInfo.getHttpResponseMessage().getStatusLine().getCode();
            case HttpResponseStatusMessage -> eventInfo.getHttpResponseMessage().getStatusLine().getMessage();
            case HttpRequestUriQueryParameter -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameter(identifier.getText(eventInfo));
            case HttpRequestUriQueryParameters -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameters();
        };
        return StringUtils.defaultString(value);
    }

    public static void setValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier, String replacementText) {
        if (messageValue.getDataDirection() == DataDirection.Request && messageValue != MessageValue.HttpRequestMessage) {
            setRequestValue(eventInfo, eventInfo.getHttpRequestMessage(), messageValue, identifier, replacementText);
        } else if (messageValue.getDataDirection() == DataDirection.Response && messageValue != MessageValue.HttpResponseMessage) {
            setResponseValue(eventInfo, eventInfo.getHttpResponseMessage(), messageValue, identifier, replacementText);
        } else {
            switch (messageValue) {
                case HttpProtocol -> eventInfo.setHttpProtocol(replacementText);
                case SourceAddress -> throw new UnsupportedOperationException("Cannot set Source Address");
                case HttpRequestMessage -> eventInfo.setHttpRequestMessage(TextUtils.stringToBytes(replacementText));
                case HttpResponseMessage -> eventInfo.setHttpResponseMessage(TextUtils.stringToBytes(replacementText));
                case DestinationPort -> eventInfo.setDestinationPort(Integer.parseInt(replacementText));
                case DestinationAddress -> eventInfo.setDestinationAddress(replacementText);
                case Url -> eventInfo.setUrl(replacementText);
            }
        }
    }

    public static void setRequestValue(EventInfo eventInfo, HttpRequestMessage requestMessage, MessageValue messageValue, VariableString identifier, String replacementText) {
        switch (messageValue) {
            case HttpRequestHeaders -> requestMessage.setHeaders(StringUtils.defaultString(replacementText));
            case HttpRequestHeader -> requestMessage.getHeaders().setHeader(identifier.getText(eventInfo), replacementText);
            case HttpRequestBody -> requestMessage.setBody(StringUtils.defaultString(replacementText));
            case HttpRequestStatusLine -> requestMessage.setStatusLine(replacementText);
            case HttpRequestCookie -> requestMessage.getHeaders().getCookies().setCookie(identifier.getText(eventInfo), replacementText);
            case HttpRequestUri -> requestMessage.getStatusLine().setUrl(replacementText);
            case HttpRequestMessage -> eventInfo.setHttpRequestMessage(TextUtils.stringToBytes(replacementText));
            case HttpRequestMethod -> requestMessage.getStatusLine().setMethod(replacementText);
            case HttpRequestUriPath -> requestMessage.getStatusLine().getUrl().setPath(StringUtils.defaultString(replacementText));
            case HttpRequestUriQueryParameter -> requestMessage.getStatusLine().getUrl().setQueryParameter(identifier.getText(eventInfo), replacementText);
            case HttpRequestUriQueryParameters -> requestMessage.getStatusLine().getUrl().setQueryParameters(StringUtils.defaultString(replacementText));
        }
    }

    public static void setResponseValue(EventInfo eventInfo, HttpResponseMessage responseMessage, MessageValue messageValue, VariableString identifier, String replacementText) {
        switch (messageValue) {
            case HttpResponseHeaders -> responseMessage.setHeaders(StringUtils.defaultString(replacementText));
            case HttpResponseHeader -> responseMessage.getHeaders().setHeader(identifier.getText(eventInfo), replacementText);
            case HttpResponseBody -> responseMessage.setBody(StringUtils.defaultString(replacementText));
            case HttpResponseStatusLine -> responseMessage.setStatusLine(replacementText);
            case HttpResponseCookie -> responseMessage.getHeaders().getCookies().setCookie(identifier.getText(eventInfo), replacementText);
            case HttpResponseMessage -> eventInfo.setHttpResponseMessage(TextUtils.stringToBytes(replacementText));
            case HttpResponseStatusCode -> responseMessage.getStatusLine().setCode(replacementText);
            case HttpResponseStatusMessage -> responseMessage.getStatusLine().setMessage(StringUtils.defaultString(replacementText));
        }
    }
}
