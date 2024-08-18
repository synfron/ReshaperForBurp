package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.vars.Variables;

public class WebSocketEventInfo<T> extends EventInfo {
    @Getter
    private final WebSocketMessageType messageType;
    @Getter
    private WebSocketDataDirection dataDirection;
    @Getter
    private final WebSocketDataDirection initialDataDirection;
    @Getter
    private final WebSocketMessageSender messageSender;
    @Getter
    private T data;

    public WebSocketEventInfo(Workspace workspace, WebSocketMessageType messageType, WebSocketDataDirection dataDirection, BurpTool burpTool, WebSocketMessageSender messageSender, HttpRequest httpRequest, Annotations annotations, T data, Variables sessionVariables) {
        super(workspace, burpTool, httpRequest, annotations, sessionVariables);
        this.messageType = messageType;
        this.initialDataDirection = dataDirection;
        this.dataDirection = dataDirection;
        this.httpProtocol = httpRequest.httpService().secure() ? "wss" : "ws";
        this.messageSender = messageSender;
        this.data = data;
    }

    public void setDataDirection(WebSocketDataDirection dataDirection) {
        this.dataDirection = dataDirection;
        changed = true;
    }

    @SuppressWarnings("unchecked")
    public void setText(String text) {
        if (data instanceof String) {
            this.data = (T) text;
        } else {
            this.data = (T)encoder.encode(text);
        }
        changed = true;
    }

    public String getText() {
        if (data instanceof String) {
            return (String)data;
        } else {
            return encoder.decode((byte[])data);
        }
    }

    @Override
    public boolean isSecure() {
        return StringUtils.equalsIgnoreCase("wss", httpProtocol);
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.WebSocket;
    }
}
