package synfron.reshaper.burp.core;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import burp.api.montoya.proxy.http.*;
import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpiringMap;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.vars.Variables;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpConnector implements
        SessionHandlingAction,
        ProxyRequestHandler,
        ProxyResponseHandler,
        HttpHandler {

    @Getter
    private RulesEngine rulesEngine = new RulesEngine();
    private Workspace workspace;
    private ServerSocket serverSocket;
    private final Map<String, HttpEventInfo> continuationMap = ExpiringMap.builder()
            .expiration(30, TimeUnit.SECONDS).build();
    private final Map<String, Variables> sessionVariableMap = ExpiringMap.builder()
            .expiration(1, TimeUnit.DAYS).build();
    private ExecutorService serverExecutor;
    private final String dataDirectionWarning = "Sanity Check - Warning: The %s changed but the data direction is set to %s. Your changes may have no impact. Consider using 'When Data Direction' or 'Then Set Data Direction' to restrict or change the data direction.";
    private final String interceptWarning = "Sanity Check - Warning: Cannot intercept unless the message is captured from the Proxy tool.";
    private final String interceptAndDropWarning = "Sanity Check - Warning: Cannot both intercept and drop the same message.";
    private boolean activated = true;

    public HttpConnector(Workspace workspace) {

        this.workspace = workspace;
    }

    public void init() {
        activated = true;
        createServer();
    }

    public void unload() {
        activated = false;
        closeServer();
        workspace = null;
        rulesEngine = null;
    }

    private void closeServer() {
        try {
            if (serverExecutor != null) {
                serverExecutor.shutdownNow();
                serverSocket.close();
            }
        } catch (Exception ignored) {}
        finally {
            serverExecutor = null;
            serverSocket = null;
        }
    }

    private void processServerConnection(Socket socket) {
        serverExecutor.execute(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedReader.readLine();
                String messageId = getReshaperId(bufferedReader.readLine());
                HttpEventInfo eventInfo = continuationMap.get(messageId);
                if (eventInfo.isShouldDrop()) {
                    continuationMap.remove(messageId);
                    close(socket);
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write(eventInfo.getHttpResponseMessage().getValue());
                bufferedOutputStream.flush();
            } catch (Exception e) {
                if (activated) {
                    Log.get(workspace).withMessage("Event processing failed").withException(e).logErr();
                }
            } finally {
                close(socket);
            }
        });
    }

    private void createServer() {
        try {
            serverExecutor = Executors.newCachedThreadPool();
            serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
            serverExecutor.execute(() -> {
                while (activated) {
                    try {
                        Socket socket = serverSocket.accept();
                        processServerConnection(socket);
                    } catch (Exception e) {
                        if (activated) {
                            Log.get(workspace).withMessage("Server accept new connection failed").withException(e).logErr();
                        }
                    }
                }
            });
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    private void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            if (activated) {
                Log.get(workspace).withMessage("Failed closing stream").withException(ex).logErr();
            }
        }
    }

    private HttpEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, String messageId, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations) {
        return asEventInfo(
                messageIsRequest,
                burpTool,
                messageId,
                httpRequest,
                httpResponse,
                annotations,
                null,
                null
        );
    }

    private HttpEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, String messageId, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, InetAddress sourceIpAddress) {
        if (!messageIsRequest &&
                httpRequest.httpService().port() == serverSocket.getLocalPort() &&
                httpRequest.httpService().host().equals(serverSocket.getInetAddress().getHostAddress())
        ) {
            HttpEventInfo requestEventInfo = continuationMap.remove(messageId);
            httpRequest = requestEventInfo.getInitialHttpRequest();
        }
        Variables sessionVariables = !messageIsRequest ? Variables.defaultVariables(sessionVariableMap.get(messageId)) : new Variables();
        HttpEventInfo eventInfo = new HttpEventInfo(
                workspace, messageIsRequest ? HttpDataDirection.Request : HttpDataDirection.Response,
                burpTool,
                messageId,
                httpRequest,
                httpResponse,
                annotations,
                proxyName,
                sourceIpAddress != null ? sourceIpAddress.getHostAddress() : "burp::",
                sessionVariables
        );
        eventInfo.getDiagnostics().setEventEnabled(workspace.getGeneralSettings().isEnableEventDiagnostics());
        return eventInfo;
    }

    private String getReshaperId(String header) {
        if (!header.startsWith("Reshaper-ID:")) {
            throw new InputMismatchException("No Reshaper-ID found");
        }
        return header.split(":", 2)[1].trim();
    }

    private EventResult processEvent(boolean isRequest, HttpEventInfo eventInfo, boolean isIntercept) {
        EventResult eventResult = new EventResult(eventInfo);
        Workspaces.get().setCurrentWorkspace(workspace);
        try {
            rulesEngine.run(eventInfo);
            storeSessionVariables(eventInfo);
            if (eventInfo.isChanged()) {
                sanityCheck(eventInfo);
                if (eventInfo.getDataDirection() == HttpDataDirection.Request) {
                    if (eventInfo.isShouldDrop()) {
                        if (isIntercept) {
                            eventResult.setInterceptResponse(InterceptResponse.Drop);
                        } else {
                            sendToSelf(eventInfo);
                        }
                    } else {
                        eventResult.setInterceptResponse(eventInfo.getDefaultInterceptResponse());
                    }
                } else if (isRequest && eventInfo.getDataDirection() == HttpDataDirection.Response) {
                    sendToSelf(eventInfo);
                    if (isIntercept) {
                        eventResult.setInterceptResponse(InterceptResponse.Disable);
                    }
                } else if (!isRequest && isIntercept) {
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

    private void storeSessionVariables(HttpEventInfo eventInfo) {
        Variables sessionVariables = eventInfo.getSessionVariables();
        if (sessionVariables.size() > 0 && eventInfo.getInitialDataDirection() == HttpDataDirection.Request && !eventInfo.isShouldDrop()) {
            sessionVariableMap.put(eventInfo.getMessageId(), sessionVariables);
        } else if (eventInfo.getInitialDataDirection() == HttpDataDirection.Response) {
            sessionVariableMap.remove(eventInfo.getMessageId());
        }
    }

    private void sanityCheck(HttpEventInfo eventInfo) {
        if (workspace.getGeneralSettings().isEnableSanityCheckWarnings()) {
            if (eventInfo.isRequestChanged() && eventInfo.getDataDirection() == HttpDataDirection.Response) {
                Log.get(workspace).withMessage(String.format(dataDirectionWarning, "request", "Response")).log();
            }
            if (eventInfo.isResponseChanged() && eventInfo.getDataDirection() == HttpDataDirection.Request) {
                Log.get(workspace).withMessage(String.format(dataDirectionWarning, "response", "Request")).log();
            }
            if (eventInfo.getDefaultInterceptResponse() == InterceptResponse.Intercept) {
                if (eventInfo.isShouldDrop()) {
                    Log.get(workspace).withMessage(interceptAndDropWarning).log();
                }
                if (eventInfo.getBurpTool() != BurpTool.Proxy) {
                    Log.get(workspace).withMessage(interceptWarning).log();
                }
            }
        }
    }

    private void sendToSelf(HttpEventInfo eventInfo) {
        HttpRequestMessage requestMessage = eventInfo.getHttpRequestMessage();
        List<String> headers = requestMessage.getHeaders().getValue();
        headers.add(0, "Reshaper-ID: " + eventInfo.getMessageId());
        byte[] modifiedRequest = ObjectUtils.asHttpMessage(
                requestMessage.getStatusLine().getValue(),
                headers,
                requestMessage.getBody().getValue()
        );
        HttpRequest httpRequest = HttpRequest.httpRequest(
                HttpService.httpService(
                        serverSocket.getInetAddress().getHostAddress(),
                        serverSocket.getLocalPort(),
                        false
                ),
                ByteArray.byteArray(modifiedRequest)
        );
        continuationMap.put(eventInfo.getMessageId(), eventInfo);
        eventInfo.setHttpRequestOverride(httpRequest);
    }

    private BurpTool getBurpToolIfEnabled(ToolType toolType) {
        BurpTool burpTool = BurpTool.from(toolType);
        return burpTool != null && workspace.getGeneralSettings().isCapture(burpTool) ? burpTool : null;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        if (workspace == null) ProxyRequestReceivedAction.continueWith(interceptedRequest);
        if (workspace.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            HttpEventInfo eventInfo = asEventInfo(true, BurpTool.Proxy, getMessageId(BurpTool.Proxy, interceptedRequest.messageId()), interceptedRequest, null, interceptedRequest.annotations(), interceptedRequest.listenerInterface(), interceptedRequest.sourceIpAddress());
            return processEvent(true, eventInfo, true).asProxyRequestAction();
        }
        else {
            return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations());
        }
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        if (workspace == null) RequestToBeSentAction.continueWith(requestToBeSent);
        if (!requestToBeSent.toolSource().isFromTool(ToolType.PROXY)) {
            BurpTool burpTool = getBurpToolIfEnabled(requestToBeSent.toolSource().toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                HttpEventInfo eventInfo = asEventInfo(true, burpTool, getMessageId(burpTool, requestToBeSent.messageId()), requestToBeSent, null, requestToBeSent.annotations());
                processEvent(true, eventInfo, false);
                return RequestToBeSentAction.continueWith(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
            }
        }
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData actionData) {
        if (workspace == null) ActionResult.actionResult(actionData.request());
        HttpEventInfo eventInfo = asEventInfo(true, BurpTool.Session, null, actionData.request(), null, actionData.annotations());
        eventInfo.setMacros(CollectionUtils.defaultIfNull(actionData.macroRequestResponses()));
        processEvent(true, eventInfo, false);
        return ActionResult.actionResult(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest, interceptedRequest.annotations());
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        if (workspace == null) ProxyResponseReceivedAction.continueWith(interceptedResponse);
        if (workspace.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            HttpEventInfo eventInfo = asEventInfo(false, BurpTool.Proxy, getMessageId(BurpTool.Proxy, interceptedResponse.messageId()), interceptedResponse.initiatingRequest(), interceptedResponse, interceptedResponse.annotations(), interceptedResponse.listenerInterface(), null);
            return processEvent(false, eventInfo, true).asProxyResponseAction();
        }
        else {
            return ProxyResponseReceivedAction.continueWith(interceptedResponse);
        }
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (workspace == null) ResponseReceivedAction.continueWith(responseReceived);
        if (responseReceived.toolSource().toolType() != ToolType.PROXY) {
            BurpTool burpTool = getBurpToolIfEnabled(responseReceived.toolSource().toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                HttpEventInfo eventInfo = asEventInfo(false, burpTool, getMessageId(burpTool, responseReceived.messageId()), responseReceived.initiatingRequest(), responseReceived, responseReceived.annotations());
                processEvent(false, eventInfo, false);
                return ResponseReceivedAction.continueWith(eventInfo.asHttpResponse(), eventInfo.getAnnotations());
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }

    private String getMessageId(BurpTool burpTool, int messageId) {
        return String.format("%s_%s", burpTool, messageId);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }

    @Override
    public String name() {
        return "Reshaper - " + workspace.getWorkspaceName();
    }

    public static class EventResult {
        private final HttpEventInfo eventInfo;
        @Getter @Setter
        private InterceptResponse interceptResponse;

        public EventResult(HttpEventInfo eventInfo) {
            this.eventInfo = eventInfo;
            interceptResponse = InterceptResponse.UserDefined;
        }

        public HttpRequest getRequest() {
            return eventInfo.asHttpRequest();
        }

        public Annotations getAnnotation() {
            return eventInfo.getAnnotations();
        }

        public HttpResponse getResponse() {
            return eventInfo.asHttpResponse();
        }

        public ProxyRequestReceivedAction asProxyRequestAction() {
            return switch (interceptResponse) {
                case UserDefined -> ProxyRequestReceivedAction.continueWith(getRequest(), getAnnotation());
                case Drop -> ProxyRequestReceivedAction.drop();
                case Intercept -> ProxyRequestReceivedAction.intercept(getRequest(), getAnnotation());
                case Disable -> ProxyRequestReceivedAction.doNotIntercept(getRequest(), getAnnotation());
            };
        }

        public ProxyResponseReceivedAction asProxyResponseAction() {
            return switch (interceptResponse) {
                case UserDefined -> ProxyResponseReceivedAction.continueWith(getResponse(), getAnnotation());
                case Drop -> ProxyResponseReceivedAction.drop();
                case Intercept -> ProxyResponseReceivedAction.intercept(getResponse(), getAnnotation());
                case Disable -> ProxyResponseReceivedAction.doNotIntercept(getResponse(), getAnnotation());
            };
        }
    }

}
