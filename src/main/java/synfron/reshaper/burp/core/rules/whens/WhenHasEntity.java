package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.vars.VariableString;

public class WhenHasEntity extends When<WhenHasEntity> {

    @Getter @Setter
    private MessageValue messageValue = MessageValue.HttpRequestBody;
    @Getter @Setter
    private VariableString identifier;

    @Override
    public boolean isMatch(EventInfo eventInfo)
    {
        boolean matches = false;
        switch (messageValue)
        {
            case SourceAddress:
            case DestinationAddress:
            case DestinationPort:
            case HttpProtocol:
                matches = true;;
                break;
            case HttpRequestMessage:
                matches = eventInfo.getHttpRequestMessage().getValue().length > 0;
                break;
            case HttpRequestMethod:
                matches = eventInfo.getHttpRequestMessage().getStatusLine().getMethod().length() > 0;
                break;
            case HttpRequestUri:
                matches = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue().length() > 0;
                break;
            case HttpRequestStatusLine:
                matches = eventInfo.getHttpRequestMessage().getStatusLine().getValue().length() > 0;
                break;
            case HttpResponseMessage:
                matches = eventInfo.getHttpResponseMessage().getValue().length > 0;
                break;
            case HttpResponseStatusLine:
                matches = eventInfo.getHttpResponseMessage().getStatusLine().getValue().length() > 0;
                break;
            case HttpResponseStatusCode:
                matches = eventInfo.getHttpResponseMessage().getStatusLine().getCode().length() > 0;
                break;
            case HttpResponseStatusMessage:
                matches = eventInfo.getHttpResponseMessage().getStatusLine().getMessage().length() > 0;
                break;
            case HttpRequestBody:
                matches = eventInfo.getHttpRequestMessage().getBody().hasValue();
                break;
            case HttpResponseBody:
                matches = eventInfo.getHttpResponseMessage().getBody().hasValue();
            break;
            case HttpRequestHeaders:
                matches = eventInfo.getHttpRequestMessage().getHeaders().getCount() > 0;
                break;
            case HttpResponseHeaders:
                matches = eventInfo.getHttpResponseMessage().getHeaders().getCount() > 0;
                break;
            case HttpRequestHeader:
                matches = eventInfo.getHttpRequestMessage().getHeaders().contains(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpResponseHeader:
                matches = eventInfo.getHttpResponseMessage().getHeaders().contains(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpRequestCookie:
                matches = eventInfo.getHttpRequestMessage().getHeaders().getCookies().contains(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpResponseCookie:
                matches = eventInfo.getHttpResponseMessage().getHeaders().getCookies().contains(identifier.getText(eventInfo.getVariables()));
                break;
            case HttpRequestUriPath:
                matches = StringUtils.isNotEmpty(eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getPath());
                break;
            case HttpRequestUriQueryParameters:
                matches = StringUtils.isNotEmpty(eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameters());
                break;
            case HttpRequestUriQueryParameter:
                matches = eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParameter(identifier.getText(eventInfo.getVariables())) != null;
                break;
        }

        return matches;
    }

    @Override
    public RuleOperationType<WhenHasEntity> getType() {
        return WhenType.HasEntity;
    }
}
