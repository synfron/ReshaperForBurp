package synfron.reshaper.burp.core.messages;

import burp.BurpExtender;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.InterceptResponse;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.rules.diagnostics.Diagnostics;
import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;
import synfron.reshaper.burp.core.utils.UrlUtils;
import synfron.reshaper.burp.core.vars.Variables;

public abstract class EventInfo {
    @Getter
    protected final HttpRequest initialHttpRequest;
    @Getter @Setter
    private Annotations annotations;
    @Getter
    protected final BurpTool burpTool;
    @Getter
    protected String destinationAddress;
    @Getter
    protected Integer destinationPort;
    @Getter
    protected String httpProtocol;
    @Getter
    protected boolean shouldDrop;
    @Getter
    protected InterceptResponse defaultInterceptResponse = InterceptResponse.UserDefined;
    @Getter
    protected HttpRequestMessage httpRequestMessage;
    @Getter
    protected final Variables variables = new Variables();
    @Getter
    private final Variables sessionVariables;
    @Getter
    protected final Encoder encoder = new Encoder(BurpExtender.getGeneralSettings().getDefaultEncoding());
    protected boolean changed;
    @Getter
    protected final IDiagnostics diagnostics = new Diagnostics();

    public EventInfo(BurpTool burpTool, HttpRequest httpRequest, Annotations annotations, Variables sessionVariables) {
        this.sessionVariables = sessionVariables;
        this.burpTool = burpTool;
        this.initialHttpRequest = httpRequest;
        this.annotations = annotations;
        httpRequestMessage = new HttpRequestMessage(httpRequest, encoder);
        destinationPort = httpRequest.httpService().port();
        destinationAddress = httpRequest.httpService().host();
    }

    protected EventInfo(EventInfo sourceEventInfo) {
        this.sessionVariables = sourceEventInfo.getSessionVariables();
        this.burpTool = sourceEventInfo.getBurpTool();
        this.initialHttpRequest = null;
        this.annotations = null;
        this.encoder.setEncoding(sourceEventInfo.getEncoder().getEncoding(), sourceEventInfo.getEncoder().isAutoSet());
        httpRequestMessage = new HttpRequestMessage(sourceEventInfo.getHttpRequestMessage().getValue(), encoder);
        httpProtocol = sourceEventInfo.getHttpProtocol();
        destinationPort = sourceEventInfo.getDestinationPort();
        destinationAddress = sourceEventInfo.getDestinationAddress();
    }

    public abstract ProtocolType getProtocolType();

    public void setShouldDrop(boolean shouldDrop) {
        this.shouldDrop = shouldDrop;
        changed = true;
    }

    public void setDefaultInterceptResponse(InterceptResponse defaultInterceptResponse) {
        this.defaultInterceptResponse = defaultInterceptResponse;
        changed = true;
    }

    public boolean isChanged() {
        return changed ||
                (httpRequestMessage != null && httpRequestMessage.isChanged());
    }

    public abstract boolean isSecure();

    public String getUrl() {
        String url;
        try {
            url = UrlUtils.getUrl(
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
