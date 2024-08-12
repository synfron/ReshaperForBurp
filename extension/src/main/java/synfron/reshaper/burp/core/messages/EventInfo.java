package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.InterceptResponse;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.rules.diagnostics.Diagnostics;
import synfron.reshaper.burp.core.rules.diagnostics.IDiagnostics;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.utils.UrlUtils;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.List;

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
    @Getter @Setter
    protected List<HttpRequestResponse> macros = List.of();
    @Getter
    protected final Variables variables = new Variables();
    @Getter
    protected final Workspace workspace;
    @Getter
    private final Variables sessionVariables;
    @Getter
    protected final Encoder encoder;
    protected boolean changed;
    @Getter
    protected final IDiagnostics diagnostics;

    public EventInfo(Workspace workspace, BurpTool burpTool, HttpRequest httpRequest, Annotations annotations, Variables sessionVariables) {
        this.workspace = workspace;
        encoder = new Encoder(workspace.getGeneralSettings().getDefaultEncoding());
        diagnostics = new Diagnostics(workspace);
        this.sessionVariables = sessionVariables;
        this.burpTool = burpTool;
        this.initialHttpRequest = httpRequest;
        this.annotations = annotations;
        httpRequestMessage = new HttpRequestMessage(workspace, httpRequest, encoder);
        destinationPort = httpRequest.httpService().port();
        destinationAddress = httpRequest.httpService().host();
    }

    protected EventInfo(EventInfo sourceEventInfo) {
        this.workspace = sourceEventInfo.workspace;
        encoder = new Encoder(workspace.getGeneralSettings().getDefaultEncoding());
        diagnostics = new Diagnostics(workspace);
        this.sessionVariables = sourceEventInfo.getSessionVariables();
        this.burpTool = sourceEventInfo.getBurpTool();
        this.initialHttpRequest = null;
        this.annotations = null;
        this.encoder.setEncoding(sourceEventInfo.getEncoder().getEncoding(), sourceEventInfo.getEncoder().isAutoSet());
        httpRequestMessage = new HttpRequestMessage(workspace, sourceEventInfo.getHttpRequestMessage().getValue(), encoder);
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
