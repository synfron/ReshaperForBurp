package synfron.reshaper.burp.core.messages;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.proxy.websocket.ProxyWebSocket;
import burp.api.montoya.websocket.WebSocket;

import java.util.function.BiConsumer;

public class WebSocketMessageSender {

    private final BiConsumer<WebSocketDataDirection, byte[]> binaryMessageSender;

    private final BiConsumer<WebSocketDataDirection, String> textMessageSender;

    public WebSocketMessageSender(ProxyWebSocket webSocket) {
        binaryMessageSender = (direction, data) -> webSocket.sendBinaryMessage(ByteArray.byteArray(data), direction.toDirection());
        textMessageSender = (direction, data) -> webSocket.sendTextMessage(data, direction.toDirection());
    }

    public WebSocketMessageSender(WebSocket webSocket) {
        binaryMessageSender = (direction, data) -> {
            if (direction == WebSocketDataDirection.Client) {
                throw new UnsupportedOperationException("Can only send client messages for Proxy WebSocket connections");
            }
            webSocket.sendBinaryMessage(ByteArray.byteArray(data));
        };
        textMessageSender = (direction, data) -> {
            if (direction == WebSocketDataDirection.Client) {
                throw new UnsupportedOperationException("Can only send client messages for Proxy WebSocket connections");
            }
            webSocket.sendTextMessage(data);
        };
    }

    public void send(WebSocketEventInfo<?> eventInfo, WebSocketDataDirection dataDirection, WebSocketMessageType messageType, String data) {
        switch (messageType) {
            case Text:
                textMessageSender.accept(dataDirection, data);
                break;
            case Binary:
                binaryMessageSender.accept(dataDirection, eventInfo.getEncoder().encode(data));
                break;
        }
    }
}
