package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.http.HttpResponseMessage;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.utils.Url;
import synfron.reshaper.burp.core.vars.Variables;

public class HttpEventInfo extends EventInfo {
    @Getter
    private final HttpResponse initialHttpResponse;
    @Getter
    private final HttpDataDirection initialDataDirection;
    @Getter
    private HttpDataDirection dataDirection;
    @Getter
    private HttpResponseMessage httpResponseMessage;
    @Getter
    protected final String proxyName;
    @Getter
    protected final String sourceAddress;
    @Getter
    private String messageId;
    private HttpRequest httpRequestOverride;

    public HttpEventInfo(Workspace workspace, HttpDataDirection dataDirection, BurpTool burpTool, String messageId, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, String sourceAddress, Variables sessionVariables) {
        super(workspace, burpTool, httpRequest, annotations, sessionVariables);
        this.initialDataDirection = dataDirection;
        this.dataDirection = dataDirection;
        this.messageId = messageId;
        this.httpProtocol = httpRequest.httpService().secure() ? "https" : "http";
        this.initialHttpResponse = httpResponse;
        this.httpResponseMessage = new HttpResponseMessage(workspace, httpResponse, encoder);
        this.sourceAddress = sourceAddress;
        this.proxyName = proxyName;
    }

    public HttpEventInfo(Workspace workspace, HttpDataDirection dataDirection, BurpTool burpTool, String messageId, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, Variables sessionVariables) {
        this(workspace, dataDirection, burpTool, messageId, httpRequest, httpResponse, annotations, null, "burp::", sessionVariables);
    }

    public HttpEventInfo(EventInfo sourceRequestEventInfo) {
        super(sourceRequestEventInfo);
        this.dataDirection = HttpDataDirection.Request;
        this.initialDataDirection = HttpDataDirection.Request;
        this.initialHttpResponse = null;
        this.httpResponseMessage = new HttpResponseMessage(sourceRequestEventInfo.workspace, (byte[]) null, encoder);
        this.sourceAddress = "burp::";
        this.proxyName = null;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.Http;
    }

    public void setDataDirection(HttpDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        changed = true;
    }

    public void setHttpRequestMessage(byte[] request) {
        httpRequestMessage = new HttpRequestMessage(workspace, request, encoder);
        changed = true;
    }

    public void setHttpResponseMessage(byte[] response) {
        httpResponseMessage = new HttpResponseMessage(workspace, response, encoder);
        changed = true;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
        changed = true;
    }

    public void setDestinationPort(Integer destinationPort) {
        this.destinationPort = destinationPort;
        changed = true;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
        changed = true;
    }

    public void setUrl(String urlStr) {
        Url url = new Url(urlStr);
        setHttpProtocol(url.getProtocol());
        setDestinationAddress(url.getHost());
        setDestinationPort(url.getPort() > 0 ? url.getPort() : url.getDefaultPort());
        getHttpRequestMessage().setUrl(url);
    }

    @Override
    public boolean isChanged() {
        return super.isChanged() ||
                (httpResponseMessage != null && httpResponseMessage.isChanged());
    }

    public boolean isRequestChanged() {
        return httpRequestMessage != null && httpRequestMessage.isChanged();
    }

    public boolean isResponseChanged() {
        return httpResponseMessage != null && httpResponseMessage.isChanged();
    }

    @Override
    public boolean isSecure() {
        return StringUtils.equalsIgnoreCase("https", httpProtocol);
    }

    public HttpRequest asHttpRequest() {
        return httpRequestOverride != null ? httpRequestOverride : (isChanged() || initialHttpRequest == null ?
                httpRequestMessage.asAdjustedHttpRequest()
                        .withService(HttpService.httpService(
                                destinationAddress,
                                destinationPort,
                                isSecure()
                        )) :
                initialHttpRequest);
    }

    public void setHttpRequestOverride(HttpRequest httpRequest) {
        this.httpRequestOverride = httpRequest;
    }

    public HttpResponse asHttpResponse() {
        return isChanged() || initialHttpResponse == null ?
                HttpResponse.httpResponse(ByteArray.byteArray(httpResponseMessage.getValue())) :
                initialHttpResponse;
    }

}
