package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.http.HttpResponseMessage;
import synfron.reshaper.burp.core.utils.Url;

public class HttpEventInfo extends EventInfo {
    @Getter
    private final HttpResponse initialHttpResponse;
    @Getter @Setter
    private Annotations annotations;
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
    private HttpRequest httpRequestOverride;

    public HttpEventInfo(HttpDataDirection dataDirection, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, String sourceAddress) {
        super(burpTool, httpRequest);
        this.initialDataDirection = dataDirection;
        this.dataDirection = dataDirection;
        this.httpProtocol = httpRequest.httpService().secure() ? "https" : "http";
        this.initialHttpResponse = httpResponse;
        this.annotations = annotations;
        this.httpResponseMessage = new HttpResponseMessage(httpResponse, encoder);
        this.sourceAddress = sourceAddress;
        this.proxyName = proxyName;
    }

    public HttpEventInfo(HttpDataDirection dataDirection, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations) {
        this(dataDirection, burpTool, httpRequest, httpResponse, annotations, null, "burp::");
    }

    public HttpEventInfo(EventInfo sourceRequestEventInfo) {
        super(sourceRequestEventInfo);
        this.dataDirection = HttpDataDirection.Request;
        this.initialDataDirection = HttpDataDirection.Request;
        this.initialHttpResponse = null;
        this.annotations = null;
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
        httpRequestMessage = new HttpRequestMessage(request, encoder);
        changed = true;
    }

    public void setHttpResponseMessage(byte[] response) {
        httpResponseMessage = new HttpResponseMessage(response, encoder);
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
                HttpRequest.httpRequest(ByteArray.byteArray(httpRequestMessage.getValue()))
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
