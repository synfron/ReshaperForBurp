package synfron.reshaper.burp.core.messages;

import burp.api.montoya.http.message.requests.HttpRequest;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.function.BiConsumer;

public class WebSocketEventInfo<T> extends EventInfo {
    @Getter
    private final WebSocketMessageType messageType;
    @Getter
    private final Variables sessionVariables;
    @Getter
    private WebSocketDataDirection dataDirection;
    @Getter
    private final WebSocketDataDirection initialDataDirection;
    @Getter
    private BiConsumer<WebSocketDataDirection, String> messageSender;
    @Getter
    private T data;

    public WebSocketEventInfo(WebSocketMessageType messageType, Variables sessionVariables, WebSocketDataDirection dataDirection, BurpTool burpTool, BiConsumer<WebSocketDataDirection, String> messageSender, HttpRequest httpRequest, T data) {
        super(burpTool, httpRequest);
        this.messageType = messageType;
        this.sessionVariables = sessionVariables;
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
