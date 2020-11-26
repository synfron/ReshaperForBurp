package synfron.reshaper.burp.core.messages;

import burp.IInterceptedProxyMessage;
import lombok.Getter;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.vars.Variables;

public class EventInfo {
    @Getter
    private final IInterceptedProxyMessage proxyMessage;
    @Getter
    private DataDirection dataDirection;
    @Getter
    private String proxyName;
    @Getter
    private String sourceAddress;
    @Getter
    private String destinationAddress;
    @Getter
    private Integer destinationPort;
    @Getter
    private String httpProtocol;
    @Getter
    private HttpRequestMessage httpRequestMessage;
    @Getter
    private HttpResponseMessage httpResponseMessage;
    @Getter
    private Variables variables = new Variables();
    private boolean changed;

    public EventInfo(DataDirection dataDirection, IInterceptedProxyMessage proxyMessage) {
        this.dataDirection = dataDirection;
        this.proxyMessage = proxyMessage;
        httpRequestMessage = new HttpRequestMessage(proxyMessage.getMessageInfo().getRequest());
        httpResponseMessage = new HttpResponseMessage(proxyMessage.getMessageInfo().getResponse());
        proxyName = proxyMessage.getListenerInterface();
        httpProtocol = proxyMessage.getMessageInfo().getHttpService().getProtocol();
        sourceAddress = proxyMessage.getClientIpAddress().getHostAddress();
        destinationPort = proxyMessage.getMessageInfo().getHttpService().getPort();
        destinationAddress = proxyMessage.getMessageInfo().getHttpService().getHost();
    }

    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
        changed = true;
    }

    public void setHttpRequestMessage(byte[] request) {
        httpRequestMessage = new HttpRequestMessage(request);
        changed = true;
    }

    public void setHttpResponseMessage(byte[] response) {
        httpResponseMessage = new HttpResponseMessage(response);
        changed = true;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
        changed = true;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
        changed = true;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
        changed = true;
    }

    public boolean isChanged() {
        return changed ||
                (httpRequestMessage != null && httpRequestMessage.isChanged()) ||
                (httpResponseMessage != null && httpResponseMessage.isChanged());
    }
}
