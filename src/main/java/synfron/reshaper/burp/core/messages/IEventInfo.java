package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;
import synfron.reshaper.burp.core.vars.Variables;

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

    BurpTool getBurpTool();

    DataDirection getDataDirection();

    String getProxyName();

    String getSourceAddress();

    String getDestinationAddress();

    Integer getDestinationPort();

    String getHttpProtocol();

    boolean isSecure();

    boolean isShouldDrop();

    HttpRequestMessage getHttpRequestMessage();

    HttpResponseMessage getHttpResponseMessage();

    Encoder getEncoder();

    Variables getVariables();

    IDiagnostics getDiagnostics();

    Annotations getAnnotations();

    HttpRequest asHttpRequest();

    void setHttpRequestOverride(HttpRequest httpRequest);

    HttpResponse asHttpResponse();

    HttpRequest getInitialHttpRequest();

    HttpResponse getInitialHttpResponse();
}
