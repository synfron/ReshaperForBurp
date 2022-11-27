package synfron.reshaper.burp.core;

import burp.BurpExtender;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolSource;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.http.HttpHandler;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.RequestResult;
import burp.api.montoya.http.ResponseResult;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.proxy.*;
import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpiringMap;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.settings.SettingsManager;
import synfron.reshaper.burp.core.utils.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Connector implements
        ProxyHttpRequestHandler,
        ProxyHttpResponseHandler,
        HttpHandler,
        ExtensionUnloadingHandler,
        SessionHandlingAction
{

    private static final AtomicInteger lastMessageId = new AtomicInteger(1);
    @Getter
    private final RulesEngine rulesEngine = new RulesEngine();
    private ServerSocket serverSocket;
    private final Map<Integer, IEventInfo> continuationMap = ExpiringMap.builder()
            .expiration(30, TimeUnit.SECONDS).build();
    @Getter
    private final SettingsManager settingsManager = new SettingsManager();
    private boolean activated = true;
    private ExecutorService serverExecutor;
    private final String dataDirectionWarning = "Sanity Check - Warning: The %s changed but the data direction is set to %s. Your changes may have no impact. Consider using 'When Data Direction' or 'Then Set Data Direction' to restrict or change the data direction.";

    public void init() {
        activated = true;
        settingsManager.loadSettings();
        createServer();
    }

    @Override
    public void extensionUnloaded() {
        activated = false;
        settingsManager.saveSettings();
        closeServer();
        Log.get().withMessage("Reshaper unloaded").log();
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
                int reshaperId = getReshaperId(bufferedReader.readLine());
                IEventInfo eventInfo = continuationMap.get(reshaperId);
                if (eventInfo.isShouldDrop()) {
                    continuationMap.remove(reshaperId);
                    close(socket);
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write(eventInfo.getHttpResponseMessage().getValue());
                bufferedOutputStream.flush();
            } catch (Exception e) {
                if (activated) {
                    Log.get().withMessage("Event processing failed").withException(e).logErr();
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
                            Log.get().withMessage("Server accept new connection failed").withException(e).logErr();
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
                Log.get().withMessage("Failed closing stream").withException(ex).logErr();
            }
        }
    }

    private IEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations) {
        return asEventInfo(
                messageIsRequest,
                burpTool,
                httpRequest,
                httpResponse,
                annotations,
                null,
                null
        );
    }

    private IEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, InetAddress sourceIpAddress) {
        if (!messageIsRequest &&
                httpRequest.httpService().port() == serverSocket.getLocalPort() &&
                httpRequest.httpService().host().equals(serverSocket.getInetAddress().getHostAddress())
        ) {
            int reshaperId = Integer.parseInt(httpRequest.headers().get(1).value());
            IEventInfo requestEventInfo = continuationMap.remove(reshaperId);
            httpRequest = requestEventInfo.getInitialHttpRequest();
        }
        IEventInfo eventInfo = new EventInfo(
                messageIsRequest ? DataDirection.Request : DataDirection.Response,
                burpTool,
                httpRequest,
                httpResponse,
                annotations,
                proxyName,
                sourceIpAddress != null ? sourceIpAddress.getHostAddress() : "burp::"
        );
        eventInfo.getDiagnostics().setEventEnabled(BurpExtender.getGeneralSettings().isEnableEventDiagnostics());
        return eventInfo;
    }

    private int getReshaperId(String header) {
        if (!header.startsWith("Reshaper-ID:")) {
            throw new InputMismatchException("No Reshaper-ID found");
        }
        return Integer.parseInt(header.split(":", 2)[1].trim());
    }

    private EventResult processEvent(boolean isRequest, IEventInfo eventInfo, boolean isIntercept) {
        int messageId = isRequest ? lastMessageId.getAndIncrement() : -1;
        EventResult eventResult = new EventResult(eventInfo);
        try {
            rulesEngine.run(eventInfo);
            if (eventInfo.isChanged()) {
                sanityCheck(eventInfo);
                if (eventInfo.getDataDirection() == DataDirection.Request) {
                    if (eventInfo.isShouldDrop()) {
                        if (isIntercept) {
                            eventResult.setInterceptResponse(InterceptResponse.Drop);
                        } else {
                            sendToSelf(messageId, eventInfo);
                        }
                    }
                } else if (isRequest && eventInfo.getDataDirection() == DataDirection.Response) {
                    sendToSelf(messageId, eventInfo);
                    if (isIntercept) {
                        eventResult.setInterceptResponse(InterceptResponse.Disable);
                    }
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

    private void sanityCheck(IEventInfo eventInfo) {
        if (BurpExtender.getGeneralSettings().isEnableSanityCheckWarnings()) {
            if (eventInfo.isRequestChanged() && eventInfo.getDataDirection() == DataDirection.Response) {
                Log.get().withMessage(String.format(dataDirectionWarning, "request", "Response")).log();
            }
            if (eventInfo.isResponseChanged() && eventInfo.getDataDirection() == DataDirection.Request) {
                Log.get().withMessage(String.format(dataDirectionWarning, "response", "Request")).log();
            }
        }
    }

    private void sendToSelf(int messageId, IEventInfo eventInfo) {
        HttpRequestMessage requestMessage = eventInfo.getHttpRequestMessage();
        List<String> headers = new ArrayList<>();
        headers.add(requestMessage.getStatusLine().getValue());
        headers.add(String.format("%s: %s", "Reshaper-ID", messageId));
        headers.addAll(requestMessage.getHeaders().getValue());
        HttpRequest httpRequest = HttpRequest.httpRequest(
                HttpService.httpService(
                        serverSocket.getInetAddress().getHostAddress(),
                        serverSocket.getLocalPort(),
                        false
                ),
                headers,
                ByteArray.byteArray(requestMessage.getBody().getValue())
        );
        continuationMap.put(messageId, eventInfo);
        eventInfo.setHttpRequestOverride(httpRequest);
    }

    private BurpTool getBurpToolIfEnabled(ToolType toolType) {
        BurpTool burpTool = BurpTool.getBy(toolType);
        return burpTool != null && BurpExtender.getGeneralSettings().isCapture(burpTool) ? burpTool : null;
    }

    @Override
    public RequestResult handleHttpRequest(HttpRequest request, Annotations annotations, ToolSource toolSource) {
        if (toolSource.toolType() != ToolType.PROXY) {
            BurpTool burpTool = getBurpToolIfEnabled(toolSource.toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                IEventInfo eventInfo = asEventInfo(true, burpTool, request, null, annotations);
                processEvent(true, eventInfo, false);
                return RequestResult.requestResult(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
            }
        }
        return RequestResult.requestResult(request, annotations);
    }

    @Override
    public ResponseResult handleHttpResponse(HttpResponse response, HttpRequest initiatingRequest, Annotations annotations, ToolSource toolSource) {
        if (toolSource.toolType() != ToolType.PROXY) {
            BurpTool burpTool = getBurpToolIfEnabled(toolSource.toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                IEventInfo eventInfo = asEventInfo(false, burpTool, initiatingRequest, response, annotations);
                processEvent(false, eventInfo, false);
                return ResponseResult.responseResult(eventInfo.asHttpResponse(), eventInfo.getAnnotations());
            }
        }
        return ResponseResult.responseResult(response, annotations);
    }

    @Override
    public String name() {
        return "Reshaper";
    }

    @Override
    public RequestResult handle(HttpRequest currentRequest, Annotations annotations, List<HttpRequestResponse> macroRequestResponses) {
        IEventInfo eventInfo = asEventInfo(true, BurpTool.Session, currentRequest, null, annotations);
        processEvent(true, eventInfo, false);
        return RequestResult.requestResult(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
    }

    @Override
    public RequestInitialInterceptResult handleReceivedRequest(InterceptedHttpRequest interceptedRequest, Annotations annotations) {
        if (BurpExtender.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            IEventInfo eventInfo = asEventInfo(true, BurpTool.Proxy, interceptedRequest, null, annotations, interceptedRequest.listenerInterface(), interceptedRequest.sourceIpAddress());
            return processEvent(true, eventInfo, true).asRequestInterceptResult();
        }
        else {
            return RequestInitialInterceptResult.followUserRules(interceptedRequest, annotations);
        }
    }

    @Override
    public RequestFinalInterceptResult handleRequestToIssue(InterceptedHttpRequest interceptedRequest, Annotations annotations) {
        return RequestFinalInterceptResult.continueWith(interceptedRequest, annotations);
    }

    @Override
    public ResponseInitialInterceptResult handleReceivedResponse(InterceptedHttpResponse interceptedResponse, HttpRequest initiatingRequest, Annotations annotations) {
        if (BurpExtender.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            IEventInfo eventInfo = asEventInfo(false, BurpTool.Proxy, initiatingRequest, interceptedResponse, annotations);
            return processEvent(false, eventInfo, true).asResponseInterceptResult();
        }
        else {
            return ResponseInitialInterceptResult.followUserRules(interceptedResponse, annotations);
        }
    }

    @Override
    public ResponseFinalInterceptResult handleResponseToReturn(InterceptedHttpResponse interceptedResponse, HttpRequest initiatingRequest, Annotations annotations) {
        return ResponseFinalInterceptResult.continueWith(interceptedResponse, annotations);
    }

    public class EventResult {
        private final IEventInfo eventInfo;
        @Getter @Setter
        private InterceptResponse interceptResponse;

        public EventResult(IEventInfo eventInfo) {
            this.eventInfo = eventInfo;
            interceptResponse = InterceptResponse.Continue;
        }

        public HttpRequest getRequest() {
            return eventInfo.asHttpRequest();
        }

        public HttpResponse getResponse() {
            return eventInfo.asHttpResponse();
        }

        public Annotations getAnnotations() {
            return eventInfo.getAnnotations();
        }

        public RequestInitialInterceptResult asRequestInterceptResult() {
            switch (interceptResponse) {
                case Continue:
                    return RequestInitialInterceptResult.followUserRules(getRequest(), getAnnotations());
                case Drop:
                    return RequestInitialInterceptResult.drop();
                case Disable:
                    return RequestInitialInterceptResult.doNotIntercept(getRequest(), getAnnotations());
            }
            return null;
        }

        public ResponseInitialInterceptResult asResponseInterceptResult() {
            switch (interceptResponse) {
                case Continue:
                    return ResponseInitialInterceptResult.followUserRules(getResponse(), getAnnotations());
                case Drop:
                    return ResponseInitialInterceptResult.drop();
                case Disable:
                    return ResponseInitialInterceptResult.doNotIntercept(getResponse(), getAnnotations());
            }
            return null;
        }
    }

    public enum InterceptResponse {
        Continue,
        Drop,
        Disable;
    }
}
