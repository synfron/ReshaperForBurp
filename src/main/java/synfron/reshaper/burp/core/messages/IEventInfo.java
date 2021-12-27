package synfron.reshaper.burp.core.messages;

import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;

public interface IEventInfo {
    void setDataDirection(DataDirection dataDirection);

    void setHttpRequestMessage(byte[] request);

    void setHttpResponseMessage(byte[] response);

    void setDestinationAddress(String destinationAddress);

    void setDestinationPort(int destinationPort);

    void setHttpProtocol(String httpProtocol);

    void setShouldDrop(boolean shouldDrop);

    void setUrl(String urlStr);

    boolean isChanged();

    boolean isRequestChanged();

    boolean isResponseChanged();

    String getUrl();

    burp.IHttpRequestResponse getRequestResponse();

    synfron.reshaper.burp.core.BurpTool getBurpTool();

    DataDirection getDataDirection();

    String getProxyName();

    String getSourceAddress();

    String getDestinationAddress();

    Integer getDestinationPort();

    String getHttpProtocol();

    boolean isShouldDrop();

    synfron.reshaper.burp.core.messages.entities.HttpRequestMessage getHttpRequestMessage();

    synfron.reshaper.burp.core.messages.entities.HttpResponseMessage getHttpResponseMessage();

    synfron.reshaper.burp.core.vars.Variables getVariables();

    IDiagnostics getDiagnostics();
}
