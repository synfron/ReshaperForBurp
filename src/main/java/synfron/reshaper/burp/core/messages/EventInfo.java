package synfron.reshaper.burp.core.messages;

import burp.BurpExtender;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.diagnostics.Diagnostics;
import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.vars.Variables;

import java.net.MalformedURLException;
import java.net.URL;

public class EventInfo implements IEventInfo {
    @Getter
    private final HttpRequest initialHttpRequest;
    @Getter
    private final HttpResponse initialHttpResponse;
    @Getter
    private final Annotations annotations;
    @Getter
    private final BurpTool burpTool;
    @Getter
    private DataDirection dataDirection;
    @Getter
    private final String proxyName;
    @Getter
    private final String sourceAddress;
    @Getter
    private String destinationAddress;
    @Getter
    private Integer destinationPort;
    @Getter
    private String httpProtocol;
    @Getter
    private boolean shouldDrop;
    @Getter
    private HttpRequestMessage httpRequestMessage;
    @Getter
    private HttpResponseMessage httpResponseMessage;
    @Getter
    private final Variables variables = new Variables();
    @Getter
    private final Encoder encoder = new Encoder(BurpExtender.getGeneralSettings().getDefaultEncoding());
    private boolean changed;
    @Getter
    private final IDiagnostics diagnostics = new Diagnostics();
    private HttpRequest httpRequestOverride;

    public EventInfo(DataDirection dataDirection, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, String sourceAddress) {
        this.burpTool = burpTool;
        this.dataDirection = dataDirection;
        this.initialHttpRequest = httpRequest;
        this.initialHttpResponse = httpResponse;
        this.annotations = annotations;
        httpRequestMessage = new HttpRequestMessage(httpRequest, encoder);
        httpResponseMessage = new HttpResponseMessage(httpResponse, encoder);
        httpProtocol = httpRequest.httpService().secure() ? "https" : "http";
        this.sourceAddress = sourceAddress;
        destinationPort = httpRequest.httpService().port();
        destinationAddress = httpRequest.httpService().host();
        this.proxyName = proxyName;
    }

    public EventInfo(DataDirection dataDirection, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations) {
        this(dataDirection, burpTool, httpRequest, httpResponse, annotations, null, "burp::");
    }

    public EventInfo(IEventInfo sourceEventInfo) {
        this.burpTool = sourceEventInfo.getBurpTool();
        this.dataDirection = sourceEventInfo.getDataDirection();
        this.initialHttpRequest = null;
        this.initialHttpResponse = null;
        this.annotations = null;
        this.encoder.setEncoding(sourceEventInfo.getEncoder().getEncoding(), sourceEventInfo.getEncoder().isAutoSet());
        httpRequestMessage = new HttpRequestMessage(sourceEventInfo.getHttpRequestMessage().getValue(), encoder);
        httpResponseMessage = new HttpResponseMessage(sourceEventInfo.getHttpResponseMessage().getValue(), encoder);
        httpProtocol = sourceEventInfo.getHttpProtocol();
        sourceAddress = sourceEventInfo.getSourceAddress();
        destinationPort = sourceEventInfo.getDestinationPort();
        destinationAddress = sourceEventInfo.getDestinationAddress();
        proxyName = sourceEventInfo.getProxyName();
    }

    @Override
    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        changed = true;
    }

    @Override
    public void setHttpRequestMessage(byte[] request) {
        httpRequestMessage = new HttpRequestMessage(request, encoder);
        changed = true;
    }

    @Override
    public void setHttpResponseMessage(byte[] response) {
        httpResponseMessage = new HttpResponseMessage(response, encoder);
        changed = true;
    }

    @Override
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
        changed = true;
    }

    @Override
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
        changed = true;
    }

    @Override
    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
        changed = true;
    }

    @Override
    public void setShouldDrop(boolean shouldDrop) {
        this.shouldDrop = shouldDrop;
        changed = true;
    }

    @Override
    public void setUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            setHttpProtocol(url.getProtocol());
            setDestinationAddress(url.getHost());
            setDestinationPort(url.getPort() > 0 ? url.getPort() : url.getDefaultPort());
            getHttpRequestMessage().setUrl(url);
        } catch (MalformedURLException e) {
            throw new WrappedException(e);
        }
    }

    @Override
    public boolean isChanged() {
        return changed ||
                (httpRequestMessage != null && httpRequestMessage.isChanged()) ||
                (httpResponseMessage != null && httpResponseMessage.isChanged());
    }

    @Override
    public boolean isRequestChanged() {
        return httpRequestMessage != null && httpRequestMessage.isChanged();
    }

    @Override
    public boolean isResponseChanged() {
        return httpResponseMessage != null && httpResponseMessage.isChanged();
    }

    @Override
    public String getUrl() {
        String url;
        try {
            url = ObjectUtils.getUrl(
                    getHttpProtocol().toLowerCase(),
                    getDestinationAddress(),
                    getDestinationPort(),
                    getHttpRequestMessage().getStatusLine().getUrl().getValue()
            ).toString();
        } catch (Exception e) {
            url = "[Unhandled URL]";
        }
        return url;
    }

    @Override
    public boolean isSecure() {
        return StringUtils.equalsIgnoreCase("https", httpProtocol);
    }

    @Override
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

    @Override
    public void setHttpRequestOverride(HttpRequest httpRequest) {
        this.httpRequestOverride = httpRequest;
    }

    @Override
    public HttpResponse asHttpResponse() {
        return isChanged() || initialHttpResponse == null ?
                HttpResponse.httpResponse(ByteArray.byteArray(httpRequestMessage.getValue())) :
                initialHttpResponse;
    }

}
