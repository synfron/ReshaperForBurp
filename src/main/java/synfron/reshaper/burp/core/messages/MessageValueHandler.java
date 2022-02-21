package synfron.reshaper.burp.core.messages;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.utils.GetItemPlacement;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Collections;
import java.util.List;

public class MessageValueHandler {

    public static String getValue(IEventInfo eventInfo, MessageValue messageValue, VariableString identifier, GetItemPlacement itemPlacement)
    {
        String value;
        if (messageValue.getDataDirection() == DataDirection.Request) {
            value = getRequestValue(eventInfo, eventInfo.getHttpRequestMessage(), messageValue, identifier, itemPlacement);
        } else if (messageValue.getDataDirection() == DataDirection.Response) {
            value = getResponseValue(eventInfo, eventInfo.getHttpResponseMessage(), messageValue, identifier, itemPlacement);
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

    public static String getRequestValue(IEventInfo eventInfo, HttpRequestMessage requestMessage, MessageValue messageValue, VariableString identifier, GetItemPlacement itemPlacement) {
        return switch (messageValue) {
            case HttpRequestHeaders -> requestMessage.getHeaders().getText();
            case HttpRequestHeader -> requestMessage.getHeaders().getHeader(identifier.getText(eventInfo), itemPlacement);
            case HttpRequestBody -> requestMessage.getBody().getText();
            case HttpRequestStatusLine -> requestMessage.getStatusLine().getValue();
            case HttpRequestCookie -> requestMessage.getHeaders().getCookies().getCookie(identifier.getText(eventInfo), itemPlacement);
            case HttpResponseCookie -> eventInfo.getHttpResponseMessage().getHeaders().getCookies().getCookie(identifier.getText(eventInfo), itemPlacement);
            case HttpRequestUri -> requestMessage.getStatusLine().getUrl().getValue();
            case HttpRequestMessage -> requestMessage.getText();
            case HttpRequestMethod -> requestMessage.getStatusLine().getMethod();
            case HttpRequestUriPath -> requestMessage.getStatusLine().getUrl().getPath();
            case HttpRequestUriQueryParameter -> requestMessage.getStatusLine().getUrl().getQueryParams().getQueryParameter(identifier.getText(eventInfo), itemPlacement);
            case HttpRequestUriQueryParameters -> requestMessage.getStatusLine().getUrl().getQueryParametersText();
            default -> throw new UnsupportedOperationException(String.format("Cannot get message value '%s'", messageValue));
        };
    }

    public static String getResponseValue(IEventInfo eventInfo, HttpResponseMessage responseMessage, MessageValue messageValue, VariableString identifier, GetItemPlacement itemPlacement) {
        return switch (messageValue) {
            case HttpResponseHeaders -> responseMessage.getHeaders().getText();
            case HttpResponseHeader -> responseMessage.getHeaders().getHeader(identifier.getText(eventInfo), itemPlacement);
            case HttpResponseBody -> responseMessage.getBody().getText();
            case HttpResponseStatusLine -> responseMessage.getStatusLine().getValue();
            case HttpResponseCookie -> responseMessage.getHeaders().getCookies().getCookie(identifier.getText(eventInfo), itemPlacement);
            case HttpResponseMessage -> responseMessage.getText();
            case HttpResponseStatusCode -> responseMessage.getStatusLine().getCode();
            case HttpResponseStatusMessage -> responseMessage.getStatusLine().getMessage();
            default -> throw new UnsupportedOperationException(String.format("Cannot get message value '%s'", messageValue));
        };
    }
    

    public static void setValue(IEventInfo eventInfo, MessageValue messageValue, VariableString identifier, SetItemPlacement itemPlacement, String replacementText) {
        if (messageValue.getDataDirection() == DataDirection.Request && !messageValue.isTopLevel()) {
            setRequestValue(eventInfo, eventInfo.getHttpRequestMessage(), messageValue, identifier,itemPlacement, replacementText);
        } else if (messageValue.getDataDirection() == DataDirection.Response && !messageValue.isTopLevel()) {
            setResponseValue(eventInfo, eventInfo.getHttpResponseMessage(), messageValue, identifier, itemPlacement, replacementText);
        } else {
            switch (messageValue) {
                case HttpProtocol -> eventInfo.setHttpProtocol(replacementText);
                case SourceAddress -> throw new UnsupportedOperationException("Cannot set Source Address");
                case HttpRequestMessage -> eventInfo.setHttpRequestMessage(eventInfo.getEncoder().encode(replacementText));
                case HttpResponseMessage -> eventInfo.setHttpResponseMessage(eventInfo.getEncoder().encode(replacementText));
                case DestinationPort -> eventInfo.setDestinationPort(Integer.parseInt(replacementText));
                case DestinationAddress -> eventInfo.setDestinationAddress(replacementText);
                case Url -> eventInfo.setUrl(replacementText);
            }
        }
    }

    public static void setRequestValue(IEventInfo eventInfo, HttpRequestMessage requestMessage, MessageValue messageValue, VariableString identifier, SetItemPlacement itemPlacement, String replacementText) {
        switch (messageValue) {
            case HttpRequestHeaders -> requestMessage.setHeaders(StringUtils.defaultString(replacementText));
            case HttpRequestHeader -> requestMessage.getHeaders().setHeader(identifier.getText(eventInfo), replacementText, itemPlacement);
            case HttpRequestBody -> requestMessage.setBody(StringUtils.defaultString(replacementText));
            case HttpRequestStatusLine -> requestMessage.setStatusLine(replacementText);
            case HttpRequestCookie -> requestMessage.getHeaders().getCookies().setCookie(identifier.getText(eventInfo), replacementText, itemPlacement);
            case HttpRequestUri -> requestMessage.getStatusLine().setUrl(replacementText);
            case HttpRequestMessage -> eventInfo.setHttpRequestMessage(eventInfo.getEncoder().encode(replacementText));
            case HttpRequestMethod -> requestMessage.getStatusLine().setMethod(replacementText);
            case HttpRequestUriPath -> requestMessage.getStatusLine().getUrl().setPath(StringUtils.defaultString(replacementText));
            case HttpRequestUriQueryParameter -> requestMessage.getStatusLine().getUrl().getQueryParams().setQueryParameter(identifier.getText(eventInfo), replacementText, itemPlacement);
            case HttpRequestUriQueryParameters -> requestMessage.getStatusLine().getUrl().setQueryParametersText(StringUtils.defaultString(replacementText));
        }
    }

    public static void setResponseValue(IEventInfo eventInfo, HttpResponseMessage responseMessage, MessageValue messageValue, VariableString identifier, SetItemPlacement itemPlacement, String replacementText) {
        switch (messageValue) {
            case HttpResponseHeaders -> responseMessage.setHeaders(StringUtils.defaultString(replacementText));
            case HttpResponseHeader -> responseMessage.getHeaders().setHeader(identifier.getText(eventInfo), replacementText, itemPlacement);
            case HttpResponseBody -> responseMessage.setBody(StringUtils.defaultString(replacementText));
            case HttpResponseStatusLine -> responseMessage.setStatusLine(replacementText);
            case HttpResponseCookie -> responseMessage.getHeaders().getCookies().setCookie(identifier.getText(eventInfo), replacementText, itemPlacement);
            case HttpResponseMessage -> eventInfo.setHttpResponseMessage(eventInfo.getEncoder().encode(replacementText));
            case HttpResponseStatusCode -> responseMessage.getStatusLine().setCode(replacementText);
            case HttpResponseStatusMessage -> responseMessage.getStatusLine().setMessage(StringUtils.defaultString(replacementText));
        }
    }

    public static List<String> getIdentifier(IEventInfo eventInfo, MessageValue messageValue) {
        return switch (messageValue) {
            case HttpRequestHeader -> eventInfo.getHttpRequestMessage().getHeaders().getHeaderNames();
            case HttpResponseHeader -> eventInfo.getHttpResponseMessage().getHeaders().getHeaderNames();
            case HttpRequestCookie -> eventInfo.getHttpRequestMessage().getHeaders().getCookies().getCookiesNames();
            case HttpResponseCookie -> eventInfo.getHttpResponseMessage().getHeaders().getCookies().getCookiesNames();
            case HttpRequestUriQueryParameter -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParams().getParamNames();
            default -> Collections.emptyList();
        };
    }
}
