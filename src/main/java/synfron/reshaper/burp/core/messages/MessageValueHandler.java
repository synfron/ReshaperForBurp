package synfron.reshaper.burp.core.messages;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

public class MessageValueHandler {

    public static String getValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier)
    {
        String value;
        if (messageValue.getDataDirection() == DataDirection.Request) {
            value = getRequestValue(eventInfo, eventInfo.getHttpRequestMessage(), messageValue, identifier);
        } else if (messageValue.getDataDirection() == DataDirection.Response) {
            value = getResponseValue(eventInfo, eventInfo.getHttpResponseMessage(), messageValue, identifier);
        } else {
            value = switch (messageValue) {
                case HttpProtocol -> eventInfo.getHttpProtocol();
                case Url -> eventInfo.getUrl();
                case SourceAddress -> eventInfo.getSourceAddress();
                case DestinationPort -> Integer.toString(eventInfo.getDestinationPort());
                case DestinationAddress -> eventInfo.getDestinationAddress();
                default -> throw new UnsupportedOperationException(String.format("Cannot get message value '%s'", messageValue));
            };
        }
        return StringUtils.defaultString(value);
    }

    public static String getRequestValue(EventInfo eventInfo, HttpRequestMessage requestMessage, MessageValue messageValue, VariableString identifier) {
        return switch (messageValue) {
            case HttpRequestHeaders -> requestMessage.getHeaders().getText();
            case HttpRequestHeader -> requestMessage.getHeaders().getHeader(identifier.getText(eventInfo));
            case HttpRequestBody -> requestMessage.getBody().getText();
            case HttpRequestStatusLine -> requestMessage.getStatusLine().getValue();
            case HttpRequestCookie -> requestMessage.getHeaders().getCookies().getCookie(identifier.getText(eventInfo));
            case HttpResponseCookie -> eventInfo.getHttpResponseMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo));
            case HttpRequestUri -> requestMessage.getStatusLine().getUrl().getValue();
            case HttpRequestMessage -> requestMessage.getText();
            case HttpRequestMethod -> requestMessage.getStatusLine().getMethod();
            case HttpRequestUriPath -> requestMessage.getStatusLine().getUrl().getPath();
            case HttpRequestUriQueryParameter -> requestMessage.getStatusLine().getUrl().getQueryParameter(identifier.getText(eventInfo));
            case HttpRequestUriQueryParameters -> requestMessage.getStatusLine().getUrl().getQueryParameters();
            default -> throw new UnsupportedOperationException(String.format("Cannot get message value '%s'", messageValue));
        };
    }

    public static String getResponseValue(EventInfo eventInfo, HttpResponseMessage responseMessage, MessageValue messageValue, VariableString identifier) {
        return switch (messageValue) {
            case HttpResponseHeaders -> responseMessage.getHeaders().getText();
            case HttpResponseHeader -> responseMessage.getHeaders().getHeader(identifier.getText(eventInfo));
            case HttpResponseBody -> responseMessage.getBody().getText();
            case HttpResponseStatusLine -> responseMessage.getStatusLine().getValue();
            case HttpResponseCookie -> responseMessage.getHeaders().getCookies().getCookie(identifier.getText(eventInfo));
            case HttpResponseMessage -> responseMessage.getText();
            case HttpResponseStatusCode -> responseMessage.getStatusLine().getCode();
            case HttpResponseStatusMessage -> responseMessage.getStatusLine().getMessage();
            default -> throw new UnsupportedOperationException(String.format("Cannot get message value '%s'", messageValue));
        };
    }
    

    public static void setValue(EventInfo eventInfo, MessageValue messageValue, VariableString identifier, String replacementText) {
        if (messageValue.getDataDirection() == DataDirection.Request && !messageValue.isTopLevel()) {
            setRequestValue(eventInfo, eventInfo.getHttpRequestMessage(), messageValue, identifier, replacementText);
        } else if (messageValue.getDataDirection() == DataDirection.Response && !messageValue.isTopLevel()) {
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
