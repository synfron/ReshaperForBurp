package synfron.reshaper.burp.core.messages;

import burp.api.montoya.websocket.Direction;

public enum WebSocketDataDirection {
    Server,
    Client;

    public static WebSocketDataDirection from(Direction direction) {
        return switch (direction) {
            case CLIENT_TO_SERVER -> Server;
            case SERVER_TO_CLIENT -> Client;
        };
    }

    public Direction toDirection() {
        return switch (this) {
            case Server -> Direction.CLIENT_TO_SERVER;
            case Client -> Direction.SERVER_TO_CLIENT;
        };
    }
}
