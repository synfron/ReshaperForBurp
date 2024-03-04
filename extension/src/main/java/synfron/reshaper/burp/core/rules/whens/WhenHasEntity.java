package synfron.reshaper.burp.core.rules.whens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.MessageValue;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.vars.VariableString;

public class WhenHasEntity extends When<WhenHasEntity> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter @Setter
    private MessageValue messageValue;
    @Getter @Setter
    private VariableString identifier;

    @Override
    public boolean isMatch(EventInfo eventInfo)
    {
        boolean matches = false;
        try {
            matches = switch (messageValue) {
                case SourceAddress, DestinationAddress, DestinationPort, HttpProtocol -> true;
                case HttpRequestMessage -> eventInfo.getHttpRequestMessage().getValue().length > 0;
                case HttpRequestMethod -> eventInfo.getHttpRequestMessage().getStatusLine().getMethod().length() > 0;
                case HttpRequestUri, Url -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue().length() > 0;
                case HttpRequestStatusLine -> eventInfo.getHttpRequestMessage().getStatusLine().getValue().length() > 0;
                case HttpResponseMessage -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getValue().length > 0;
                case HttpResponseStatusLine -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getStatusLine().getValue().length() > 0;
                case HttpResponseStatusCode -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getStatusLine().getCode().length() > 0;
                case HttpResponseStatusMessage -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getStatusLine().getMessage().length() > 0;
                case HttpRequestBody -> eventInfo.getHttpRequestMessage().getBody().hasValue();
                case HttpResponseBody -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getBody().hasValue();
                case HttpRequestHeaders -> eventInfo.getHttpRequestMessage().getHeaders().getCount() > 0;
                case HttpResponseHeaders -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getHeaders().getCount() > 0;
                case HttpRequestHeader -> eventInfo.getHttpRequestMessage().getHeaders().contains(identifier.getText(eventInfo));
                case HttpResponseHeader -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getHeaders().contains(identifier.getText(eventInfo));
                case HttpRequestCookie -> eventInfo.getHttpRequestMessage().getHeaders().containsCookie(identifier.getText(eventInfo));
                case HttpResponseCookie -> ((HttpEventInfo)eventInfo).getHttpResponseMessage().getHeaders().containsCookie(identifier.getText(eventInfo));
                case HttpRequestUriPath -> StringUtils.isNotEmpty(eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getPath());
                case HttpRequestUriQueryParameters -> StringUtils.isNotEmpty(eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParametersText());
                case HttpRequestUriQueryParameter -> eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getQueryParams().hasQueryParameter(identifier.getText(eventInfo));
                case WebSocketMessage -> eventInfo instanceof WebSocketEventInfo<?>;
            };
        } catch (Exception ignored) {
        }
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logHas(this, messageValue, VariableString.getTextOrDefault(eventInfo, identifier, ""), matches);

        return matches;
    }

    @Override
    public RuleOperationType<WhenHasEntity> getType() {
        return WhenType.HasEntity;
    }
}
