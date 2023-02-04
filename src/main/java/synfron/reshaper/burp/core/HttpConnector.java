package synfron.reshaper.burp.core;

import burp.BurpExtender;
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
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.ObjectUtils;

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
import java.util.concurrent.atomic.AtomicInteger;

public class HttpConnector implements
        SessionHandlingAction,
        ProxyRequestHandler,
        ProxyResponseHandler,
        HttpHandler {

    private static final AtomicInteger lastMessageId = new AtomicInteger(1);
    @Getter
    private final RulesEngine rulesEngine = new RulesEngine();
    private ServerSocket serverSocket;
    private final Map<Integer, HttpEventInfo> continuationMap = ExpiringMap.builder()
            .expiration(30, TimeUnit.SECONDS).build();
    private ExecutorService serverExecutor;
    private final String dataDirectionWarning = "Sanity Check - Warning: The %s changed but the data direction is set to %s. Your changes may have no impact. Consider using 'When Data Direction' or 'Then Set Data Direction' to restrict or change the data direction.";
    private final String interceptWarning = "Sanity Check - Warning: Cannot intercept unless the message is captured from the Proxy tool.";
    private final String interceptAndDropWarning = "Sanity Check - Warning: Cannot both intercept and drop the same message.";
    private boolean activated = true;

    public void init() {
        activated = true;
        createServer();
    }

    public void unload() {
        activated = false;
        closeServer();
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
                HttpEventInfo eventInfo = continuationMap.get(reshaperId);
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

    private HttpEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations) {
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

    private HttpEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, HttpRequest httpRequest, HttpResponse httpResponse, Annotations annotations, String proxyName, InetAddress sourceIpAddress) {
        if (!messageIsRequest &&
                httpRequest.httpService().port() == serverSocket.getLocalPort() &&
                httpRequest.httpService().host().equals(serverSocket.getInetAddress().getHostAddress())
        ) {
            int reshaperId = Integer.parseInt(httpRequest.headers().get(0).value());
            HttpEventInfo requestEventInfo = continuationMap.remove(reshaperId);
            httpRequest = requestEventInfo.getInitialHttpRequest();
        }
        HttpEventInfo eventInfo = new HttpEventInfo(
                messageIsRequest ? HttpDataDirection.Request : HttpDataDirection.Response,
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

    private EventResult processEvent(boolean isRequest, HttpEventInfo eventInfo, boolean isIntercept) {
        int messageId = isRequest ? lastMessageId.getAndIncrement() : -1;
        EventResult eventResult = new EventResult(eventInfo);
        try {
            rulesEngine.run(eventInfo);
            if (eventInfo.isChanged()) {
                sanityCheck(eventInfo);
                if (eventInfo.getDataDirection() == HttpDataDirection.Request) {
                    if (eventInfo.isShouldDrop()) {
                        if (isIntercept) {
                            eventResult.setInterceptResponse(InterceptResponse.Drop);
                        } else {
                            sendToSelf(messageId, eventInfo);
                        }
                    } else {
                        eventResult.setInterceptResponse(eventInfo.getDefaultInterceptResponse());
                    }
                } else if (isRequest && eventInfo.getDataDirection() == HttpDataDirection.Response) {
                    sendToSelf(messageId, eventInfo);
                    if (isIntercept) {
                        eventResult.setInterceptResponse(InterceptResponse.Disable);
                    }
                } else if (!isRequest && isIntercept) {
                    eventResult.setInterceptResponse(eventInfo.getDefaultInterceptResponse());
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

    private void sanityCheck(HttpEventInfo eventInfo) {
        if (BurpExtender.getGeneralSettings().isEnableSanityCheckWarnings()) {
            if (eventInfo.isRequestChanged() && eventInfo.getDataDirection() == HttpDataDirection.Response) {
                Log.get().withMessage(String.format(dataDirectionWarning, "request", "Response")).log();
            }
            if (eventInfo.isResponseChanged() && eventInfo.getDataDirection() == HttpDataDirection.Request) {
                Log.get().withMessage(String.format(dataDirectionWarning, "response", "Request")).log();
            }
            if (eventInfo.getDefaultInterceptResponse() == InterceptResponse.Intercept) {
                if (eventInfo.isShouldDrop()) {
                    Log.get().withMessage(interceptAndDropWarning).log();
                }
                if (eventInfo.getBurpTool() != BurpTool.Proxy) {
                    Log.get().withMessage(interceptWarning).log();
                }
            }
        }
    }

    private void sendToSelf(int messageId, HttpEventInfo eventInfo) {
        HttpRequestMessage requestMessage = eventInfo.getHttpRequestMessage();
        List<String> headers = requestMessage.getHeaders().getValue();
        headers.add(0, "Reshaper-ID: " + messageId);
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
        continuationMap.put(messageId, eventInfo);
        eventInfo.setHttpRequestOverride(httpRequest);
    }

    private BurpTool getBurpToolIfEnabled(ToolType toolType) {
        BurpTool burpTool = BurpTool.from(toolType);
        return burpTool != null && BurpExtender.getGeneralSettings().isCapture(burpTool) ? burpTool : null;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        if (BurpExtender.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            HttpEventInfo eventInfo = asEventInfo(true, BurpTool.Proxy, interceptedRequest, null, interceptedRequest.annotations(), interceptedRequest.listenerInterface(), interceptedRequest.sourceIpAddress());
            return processEvent(true, eventInfo, true).asProxyRequestAction();
        }
        else {
            return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations());
        }
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        if (!requestToBeSent.toolSource().isFromTool(ToolType.PROXY)) {
            BurpTool burpTool = getBurpToolIfEnabled(requestToBeSent.toolSource().toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                HttpEventInfo eventInfo = asEventInfo(true, burpTool, requestToBeSent, null, requestToBeSent.annotations());
                processEvent(true, eventInfo, false);
                return RequestToBeSentAction.continueWith(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
            }
        }
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData actionData) {
        HttpEventInfo eventInfo = asEventInfo(true, BurpTool.Session, actionData.request(), null, actionData.annotations());
        processEvent(true, eventInfo, false);
        return ActionResult.actionResult(eventInfo.asHttpRequest(), eventInfo.getAnnotations());
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        if (BurpExtender.getGeneralSettings().isCapture(BurpTool.Proxy)) {
            HttpEventInfo eventInfo = asEventInfo(false, BurpTool.Proxy, interceptedResponse.initiatingRequest(), interceptedResponse, interceptedResponse.annotations());
            return processEvent(false, eventInfo, true).asProxyResponseAction();
        }
        else {
            return ProxyResponseReceivedAction.continueWith(interceptedResponse);
        }
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (responseReceived.toolSource().toolType() != ToolType.PROXY) {
            BurpTool burpTool = getBurpToolIfEnabled(responseReceived.toolSource().toolType());
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                HttpEventInfo eventInfo = asEventInfo(false, burpTool, responseReceived.initiatingRequest(), responseReceived, responseReceived.annotations());
                processEvent(false, eventInfo, false);
                return ResponseReceivedAction.continueWith(eventInfo.asHttpResponse(), eventInfo.getAnnotations());
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }

    @Override
    public String name() {
        return "Reshaper";
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

        public HttpResponse getResponse() {
            return eventInfo.asHttpResponse();
        }

        public Annotations getAnnotations() {
            return eventInfo.getAnnotations();
        }

        public ProxyRequestReceivedAction asProxyRequestAction() {
            return switch (interceptResponse) {
                case UserDefined -> ProxyRequestReceivedAction.continueWith(getRequest(), getAnnotations());
                case Drop -> ProxyRequestReceivedAction.drop();
                case Intercept -> ProxyRequestReceivedAction.intercept(getRequest(), getAnnotations());
                case Disable -> ProxyRequestReceivedAction.doNotIntercept(getRequest(), getAnnotations());
            };
        }

        public ProxyResponseReceivedAction asProxyResponseAction() {
            return switch (interceptResponse) {
                case UserDefined -> ProxyResponseReceivedAction.continueWith(getResponse(), getAnnotations());
                case Drop -> ProxyResponseReceivedAction.drop();
                case Intercept -> ProxyResponseReceivedAction.intercept(getResponse(), getAnnotations());
                case Disable -> ProxyResponseReceivedAction.doNotIntercept(getResponse(), getAnnotations());
            };
        }
    }

}
