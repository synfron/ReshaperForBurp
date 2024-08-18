package synfron.reshaper.burp.core;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.websocket.*;
import burp.api.montoya.websocket.*;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.messages.WebSocketMessageSender;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.Variables;

public class WebSocketConnector implements
        ProxyWebSocketCreationHandler,
        WebSocketCreatedHandler {
    @Getter
    private RulesEngine rulesEngine = new RulesEngine();
    private Workspace workspace;

    public WebSocketConnector(Workspace workspace) {
        this.workspace = workspace;
    }

    private BurpTool getBurpToolIfEnabled(ToolType toolType) {
        BurpTool burpTool = BurpTool.from(toolType);
        return burpTool != null && workspace.getGeneralSettings().isCapture(burpTool) ? burpTool : null;
    }

    @Override
    public void handleWebSocketCreation(ProxyWebSocketCreation webSocketCreation) {
        if (workspace == null) return;
        if (workspace.getGeneralSettings().isCapture(BurpTool.Proxy) && workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
            webSocketCreation.proxyWebSocket().registerProxyMessageHandler(new WebSocketMessageConnector(BurpTool.Proxy, webSocketCreation.proxyWebSocket(), webSocketCreation.upgradeRequest()));
        }
    }

    @Override
    public void handleWebSocketCreated(WebSocketCreated webSocketCreated) {
        if (workspace == null) return;
        if (!webSocketCreated.toolSource().isFromTool(ToolType.PROXY)) {
            BurpTool burpTool = getBurpToolIfEnabled(webSocketCreated.toolSource().toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy && workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                webSocketCreated.webSocket().registerMessageHandler(new WebSocketMessageConnector(BurpTool.Proxy, webSocketCreated.webSocket(), webSocketCreated.upgradeRequest()));
            }
        }
    }

    public void unload() {
        rulesEngine = null;
        workspace = null;
    }

    private class WebSocketMessageConnector implements ProxyMessageHandler, MessageHandler {

        private final WebSocketMessageSender messageSender;
        private final HttpRequest httpRequest;

        private final BurpTool burpTool;

        private final Variables sessionVariables = new Variables();

        public WebSocketMessageConnector(BurpTool burpTool, ProxyWebSocket webSocket, HttpRequest httpRequest) {
            this.burpTool = burpTool;
            this.messageSender = new WebSocketMessageSender(webSocket);
            this.httpRequest = httpRequest;
        }

        public WebSocketMessageConnector(BurpTool burpTool, WebSocket webSocket, HttpRequest httpRequest) {
            this.burpTool = burpTool;
            this.messageSender = new WebSocketMessageSender(webSocket);
            this.httpRequest = httpRequest;
        }

        @Override
        public TextMessageReceivedAction handleTextMessageReceived(InterceptedTextMessage interceptedTextMessage) {
            if (workspace == null) return TextMessageReceivedAction.continueWith(interceptedTextMessage);
            if (workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                WebSocketEventInfo<String> eventInfo = asEventInfo(WebSocketMessageType.Text, interceptedTextMessage.annotations(), interceptedTextMessage.payload(), interceptedTextMessage.direction());
                return processEvent(eventInfo).asProxyTextAction();
            }
            return TextMessageReceivedAction.continueWith(interceptedTextMessage);
        }

        @Override
        public TextMessageAction handleTextMessage(TextMessage textMessage) {
            if (workspace == null) return TextMessageAction.continueWith(textMessage);
            if (workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                WebSocketEventInfo<String> eventInfo = asEventInfo(WebSocketMessageType.Text, null, textMessage.payload(), textMessage.direction());
                return processEvent(eventInfo).asTextAction();
            }
            return TextMessageAction.continueWith(textMessage);
        }

        @Override
        public BinaryMessageReceivedAction handleBinaryMessageReceived(InterceptedBinaryMessage interceptedBinaryMessage) {
            if (workspace == null) return BinaryMessageReceivedAction.continueWith(interceptedBinaryMessage);
            if (workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                WebSocketEventInfo<byte[]> eventInfo = asEventInfo(WebSocketMessageType.Binary, interceptedBinaryMessage.annotations(), interceptedBinaryMessage.payload().getBytes(), interceptedBinaryMessage.direction());
                return processEvent(eventInfo).asProxyBinaryAction();
            }
            return BinaryMessageReceivedAction.continueWith(interceptedBinaryMessage);
        }

        @Override
        public BinaryMessageAction handleBinaryMessage(BinaryMessage binaryMessage) {
            if (workspace == null) return BinaryMessageAction.continueWith(binaryMessage);
            if (workspace.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                WebSocketEventInfo<byte[]> eventInfo = asEventInfo(WebSocketMessageType.Binary, null, binaryMessage.payload().getBytes(), binaryMessage.direction());
                return processEvent(eventInfo).asBinaryAction();
            }
            return BinaryMessageAction.continueWith(binaryMessage);
        }

        private <T> WebSocketEventInfo<T> asEventInfo(WebSocketMessageType messageType, Annotations annotations, T data, Direction direction) {
            WebSocketEventInfo<T> eventInfo = new WebSocketEventInfo<>(
                    workspace, messageType,
                    WebSocketDataDirection.from(direction), burpTool, messageSender, httpRequest, annotations, data, sessionVariables
            );
            eventInfo.getDiagnostics().setEventEnabled(workspace.getGeneralSettings().isEnableEventDiagnostics());
            return eventInfo;
        }

        private <T> EventResult<T> processEvent(WebSocketEventInfo<T> eventInfo) {
            EventResult<T> eventResult = new EventResult<>(eventInfo);
            Workspaces.get().setCurrentWorkspace(workspace);
            try {
                rulesEngine.run(eventInfo);
                if (eventInfo.isChanged()) {
                    if (eventInfo.isShouldDrop()) {
                        eventResult.setInterceptResponse(InterceptResponse.Drop);
                    } else {
                        eventResult.setInterceptResponse(eventInfo.getDefaultInterceptResponse());
                    }
                }
            } catch (Exception e) {
                Log.get(workspace).withMessage("Critical Error").withException(e).logErr();
            } finally {
                if (eventInfo.getDiagnostics().hasLogs()) {
                    Log.get(workspace).withMessage(eventInfo.getDiagnostics().getLogs()).logRaw();
                }
            }
            return eventResult;
        }

        @Override
        public TextMessageToBeSentAction handleTextMessageToBeSent(InterceptedTextMessage interceptedTextMessage) {
            return TextMessageToBeSentAction.continueWith(interceptedTextMessage);
        }

        @Override
        public BinaryMessageToBeSentAction handleBinaryMessageToBeSent(InterceptedBinaryMessage interceptedBinaryMessage) {
            return BinaryMessageToBeSentAction.continueWith(interceptedBinaryMessage);
        }

        @Override
        public void onClose() {
        }
    }

    public static class EventResult<T> {
        private final WebSocketEventInfo<T> eventInfo;
        @Getter @Setter
        private InterceptResponse interceptResponse;

        public EventResult(WebSocketEventInfo<T> eventInfo) {
            this.eventInfo = eventInfo;
            interceptResponse = InterceptResponse.UserDefined;
        }

        public T getData() {
            return eventInfo.getData();
        }

        public TextMessageReceivedAction asProxyTextAction() {
            return switch (interceptResponse) {
                case UserDefined ->
                        TextMessageReceivedAction.continueWith((String) eventInfo.getData());
                case Disable -> TextMessageReceivedAction.doNotIntercept((String) eventInfo.getData());
                case Drop -> TextMessageReceivedAction.drop();
                case Intercept -> TextMessageReceivedAction.intercept((String) eventInfo.getData());
            };
        }

        public TextMessageAction asTextAction() {
            return switch (interceptResponse) {
                case UserDefined, Disable, Intercept ->
                        TextMessageAction.continueWith((String)getData());
                case Drop -> TextMessageAction.drop();
            };
        }

        public BinaryMessageReceivedAction asProxyBinaryAction() {
            return switch (interceptResponse) {
                case UserDefined ->
                        BinaryMessageReceivedAction.continueWith(ByteArray.byteArray((byte[]) eventInfo.getData()));
                case Disable -> BinaryMessageReceivedAction.doNotIntercept(ByteArray.byteArray((byte[]) eventInfo.getData()));
                case Drop -> BinaryMessageReceivedAction.drop();
                case Intercept -> BinaryMessageReceivedAction.intercept(ByteArray.byteArray((byte[]) eventInfo.getData()));
            };
        }

        public BinaryMessageAction asBinaryAction() {
            return switch (interceptResponse) {
                case UserDefined, Disable, Intercept ->
                        BinaryMessageAction.continueWith(ByteArray.byteArray((byte[]) getData()));
                case Drop -> BinaryMessageAction.drop();
            };
        }
    }
}
