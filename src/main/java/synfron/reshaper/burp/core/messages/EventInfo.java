package synfron.reshaper.burp.core.messages;

import burp.IHttpRequestResponse;
import burp.IInterceptedProxyMessage;
import lombok.Getter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.diagnostics.Diagnostics;
import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.utils.SetItemPlacement;
import synfron.reshaper.burp.core.vars.Variables;

import java.net.MalformedURLException;
import java.net.URL;

public class EventInfo implements IEventInfo {
    @Getter
    private final IHttpRequestResponse requestResponse;
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
    private boolean changed;
    @Getter
    private final IDiagnostics diagnostics = new Diagnostics();

    public EventInfo(DataDirection dataDirection, IInterceptedProxyMessage proxyMessage) {
        this.burpTool = BurpTool.Proxy;
        this.dataDirection = dataDirection;
        this.requestResponse = proxyMessage.getMessageInfo();
        httpRequestMessage = new HttpRequestMessage(requestResponse.getRequest());
        httpResponseMessage = new HttpResponseMessage(requestResponse.getResponse());
        proxyName = proxyMessage.getListenerInterface();
        httpProtocol = requestResponse.getHttpService().getProtocol();
        sourceAddress = proxyMessage.getClientIpAddress().getHostAddress();
        destinationPort = requestResponse.getHttpService().getPort();
        destinationAddress = requestResponse.getHttpService().getHost();
    }

    public EventInfo(DataDirection dataDirection, BurpTool burpTool, IHttpRequestResponse requestResponse) {
        this.burpTool = burpTool;
        this.dataDirection = dataDirection;
        this.requestResponse = requestResponse;
        httpRequestMessage = new HttpRequestMessage(requestResponse.getRequest());
        httpResponseMessage = new HttpResponseMessage(requestResponse.getResponse());
        httpProtocol = requestResponse.getHttpService().getProtocol();
        sourceAddress = "burp::";
        destinationPort = requestResponse.getHttpService().getPort();
        destinationAddress = requestResponse.getHttpService().getHost();
        proxyName = null;
    }

    @Override
    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        changed = true;
    }

    @Override
    public void setHttpRequestMessage(byte[] request) {
        httpRequestMessage = new HttpRequestMessage(request);
        changed = true;
    }

    @Override
    public void setHttpResponseMessage(byte[] response) {
        httpResponseMessage = new HttpResponseMessage(response);
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
            getHttpRequestMessage().getStatusLine().setUrl(url.getFile().startsWith("/") ? url.getFile() : "/" + url.getFile());
            getHttpRequestMessage().getHeaders().setHeader("Host", url.getAuthority(), SetItemPlacement.Only);
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
}
