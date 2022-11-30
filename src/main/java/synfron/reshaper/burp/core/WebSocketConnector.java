package synfron.reshaper.burp.core;

import burp.BurpExtender;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolSource;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.internal.ObjectFactoryLocator;
import burp.api.montoya.proxy.*;
import burp.api.montoya.websocket.*;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.WebSocketDataDirection;
import synfron.reshaper.burp.core.messages.WebSocketMessageType;
import synfron.reshaper.burp.core.messages.WebSocketEventInfo;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.Variables;

import java.util.function.BiConsumer;

public class WebSocketConnector implements ProxyWebSocketCreationHandler, WebSocketCreationHandler {
    @Getter
    private final RulesEngine rulesEngine = new RulesEngine();

    private BurpTool getBurpToolIfEnabled(ToolType toolType) {
        BurpTool burpTool = BurpTool.from(toolType);
        return burpTool != null && BurpExtender.getGeneralSettings().isCapture(burpTool) ? burpTool : null;
    }

    @Override
    public void handleWebSocketCreated(ProxyWebSocket proxyWebSocket, HttpRequest httpRequest) {
        if (BurpExtender.getGeneralSettings().isCapture(BurpTool.Proxy) && BurpExtender.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
            proxyWebSocket.registerHandler(new WebSocketMessageConnector(BurpTool.Proxy, proxyWebSocket, httpRequest));
        }
    }

    @Override
    public void handleWebSocketCreated(WebSocket webSocket, HttpRequest httpRequest, ToolSource toolSource) {
        //if (!toolSource.isFromTool(ToolType.PROXY)) {
            BurpTool burpTool = getBurpToolIfEnabled(toolSource.toolType());
            if (burpTool != null /*&& burpTool != BurpTool.Proxy*/ && BurpExtender.getGeneralSettings().isCapture(BurpTool.WebSockets)) {
                webSocket.registerHandler(new WebSocketMessageConnector(BurpTool.Proxy, webSocket, httpRequest));
            }
        //}
    }

    private class WebSocketMessageConnector implements ProxyWebSocketHandler, WebSocketHandler {

        private final BiConsumer<WebSocketDataDirection, String> messageSender;
        private final HttpRequest httpRequest;

        private final BurpTool burpTool;

        private final Variables sessionVariables = new Variables();

        public WebSocketMessageConnector(BurpTool burpTool, ProxyWebSocket webSocket, HttpRequest httpRequest) {
            this.burpTool = burpTool;
            this.messageSender = (WebSocketDataDirection dataDirection, String message) -> webSocket.sendTextMessage(message, dataDirection.toDirection());
            this.httpRequest = httpRequest;
        }

        public WebSocketMessageConnector(BurpTool burpTool, WebSocket webSocket, HttpRequest httpRequest) {
            this.burpTool = burpTool;
            this.messageSender = (WebSocketDataDirection dataDirection, String message) -> webSocket.sendTextMessage(message);
            this.httpRequest = httpRequest;
        }

        @Override
        public ProxyWebSocketInitialInterceptTextMessage handleTextMessageReceived(String text, Direction direction) {
            WebSocketEventInfo<String> eventInfo = asEventInfo(WebSocketMessageType.Text, text, direction);
            return processEvent(eventInfo).asTextProxyInterceptResult();
        }

        @Override
        public WebSocketTextMessage handleTextMessage(String text, Direction direction) {
            WebSocketEventInfo<String> eventInfo = asEventInfo(WebSocketMessageType.Text, text, direction);
            return processEvent(eventInfo).asTextInterceptResult();
        }

        @Override
        public ProxyWebSocketInitialInterceptBinaryMessage handleBinaryMessageReceived(ByteArray byteArray, Direction direction) {
            WebSocketEventInfo<byte[]> eventInfo = asEventInfo(WebSocketMessageType.Binary, byteArray.getBytes(), direction);
            return processEvent(eventInfo).asBinaryProxyInterceptResult();
        }

        @Override
        public WebSocketBinaryMessage handleBinaryMessage(ByteArray byteArray, Direction direction) {
            WebSocketEventInfo<byte[]> eventInfo = asEventInfo(WebSocketMessageType.Binary, byteArray.getBytes(), direction);
            return processEvent(eventInfo).asBinaryInterceptResult();
        }

        private <T> WebSocketEventInfo<T> asEventInfo(WebSocketMessageType messageType, T data, Direction direction) {
            WebSocketEventInfo<T> eventInfo = new WebSocketEventInfo<>(
                    messageType,
                    sessionVariables,
                    WebSocketDataDirection.from(direction),
                    burpTool,
                    messageSender,
                    httpRequest,
                    data
            );
            eventInfo.getDiagnostics().setEventEnabled(BurpExtender.getGeneralSettings().isEnableEventDiagnostics());
            return eventInfo;
        }

        private <T> EventResult<T> processEvent(WebSocketEventInfo<T> eventInfo) {
            EventResult<T> eventResult = new EventResult<>(eventInfo);
            try {
                rulesEngine.run(eventInfo);
                if (eventInfo.isChanged()) {
                    if (eventInfo.isShouldDrop()) {
                        eventResult.setInterceptResponse(InterceptResponse.Drop);
                    }
                }
            } catch (Exception e) {
                Log.get().withMessage("Critical Error").withException(e).logErr();
            } finally {
                if (eventInfo.getDiagnostics().hasLogs()) {
                    Log.get().withMessage(eventInfo.getDiagnostics().getLogs()).logRaw();
                }
            }
            return eventResult;
        }

        @Override
        public ProxyWebSocketFinalInterceptTextMessage handleTextMessageToBeIssued(String text, Direction direction) {
            return ProxyWebSocketFinalInterceptTextMessage.continueWithTextMessage(text);
        }

        @Override
        public ProxyWebSocketFinalInterceptBinaryMessage handleBinaryMessageToBeIssued(ByteArray byteArray, Direction direction) {
            return ProxyWebSocketFinalInterceptBinaryMessage.continueWithBinaryMessage(byteArray);
        }

        @Override
        public void onClose() {
            ProxyWebSocketHandler.super.onClose();
        }
    }

    public static class EventResult<T> {
        private final WebSocketEventInfo<T> eventInfo;
        @Getter @Setter
        private InterceptResponse interceptResponse;

        public EventResult(WebSocketEventInfo<T> eventInfo) {
            this.eventInfo = eventInfo;
            interceptResponse = InterceptResponse.Continue;
        }

        public T getData() {
            return eventInfo.getData();
        }

        public ProxyWebSocketInitialInterceptTextMessage asTextProxyInterceptResult() {
            return switch (interceptResponse) {
                case Continue, Disable ->
                        ObjectFactoryLocator.FACTORY.proxyWebSocketTextMessage((String) eventInfo.getData(), InitialInterceptAction.FOLLOW_USER_RULES);
                case Drop -> ProxyWebSocketInitialInterceptTextMessage.dropTextMessage();
            };
        }

        public WebSocketTextMessage asTextInterceptResult() {
            return switch (interceptResponse) {
                case Continue, Disable ->
                        WebSocketTextMessage.continueWithTextMessage((String)getData());
                case Drop -> WebSocketTextMessage.dropTextMessage();
            };
        }

        public ProxyWebSocketInitialInterceptBinaryMessage asBinaryProxyInterceptResult() {
            return switch (interceptResponse) {
                case Continue, Disable ->
                        ObjectFactoryLocator.FACTORY.proxyWebSocketBinaryMessage(ByteArray.byteArray((byte[]) eventInfo.getData()), InitialInterceptAction.FOLLOW_USER_RULES);
                case Drop -> ProxyWebSocketInitialInterceptBinaryMessage.dropBinaryMessage();
            };
        }

        public WebSocketBinaryMessage asBinaryInterceptResult() {
            return switch (interceptResponse) {
                case Continue, Disable ->
                        WebSocketBinaryMessage.continueWithBinaryMessage(ByteArray.byteArray((byte[]) getData()));
                case Drop -> WebSocketBinaryMessage.dropBinaryMessage();
            };
        }
    }
}
