package synfron.reshaper.burp.core;

import burp.*;
import lombok.Getter;
import net.jodah.expiringmap.ExpiringMap;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.settings.GeneralSettings;
import synfron.reshaper.burp.core.settings.SettingsManager;
import synfron.reshaper.burp.core.utils.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Connector implements IProxyListener, IHttpListener, IExtensionStateListener {

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
                IEventInfo eventInfo = continuationMap.remove(reshaperId);
                if (eventInfo.isShouldDrop()) {
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

    private IEventInfo asEventInfo(boolean messageIsRequest, IInterceptedProxyMessage message) {
        IEventInfo eventInfo = new EventInfo(messageIsRequest ? DataDirection.Request : DataDirection.Response, message);
        eventInfo.getDiagnostics().setEnabled(BurpExtender.getGeneralSettings().isEnableEventDiagnostics());
        return eventInfo;
    }

    private IEventInfo asEventInfo(boolean messageIsRequest, BurpTool burpTool, IHttpRequestResponse requestResponse) {
        IEventInfo eventInfo = new EventInfo(messageIsRequest ? DataDirection.Request : DataDirection.Response, burpTool, requestResponse);
        eventInfo.getDiagnostics().setEnabled(BurpExtender.getGeneralSettings().isEnableEventDiagnostics());
        return eventInfo;
    }

    private int getReshaperId(String header) {
        if (!header.startsWith("Reshaper-ID:")) {
            throw new InputMismatchException("No Reshaper-ID found");
        }
        return Integer.parseInt(header.split(":", 2)[1].trim());
    }

    private void processEvent(boolean isRequest, IEventInfo eventInfo, IInterceptedProxyMessage interceptedMessage) {
        int messageId = isRequest ? lastMessageId.getAndIncrement() : -1;
        try {
            rulesEngine.run(eventInfo);
            if (eventInfo.isChanged()) {
                sanityCheck(eventInfo);
                IHttpRequestResponse messageInfo = eventInfo.getRequestResponse();
                if (eventInfo.getDataDirection() == DataDirection.Request) {
                    messageInfo.setRequest(eventInfo.getHttpRequestMessage().getValue());
                    messageInfo.setHttpService(BurpExtender.getCallbacks().getHelpers().buildHttpService(
                            eventInfo.getDestinationAddress(),
                            eventInfo.getDestinationPort(),
                            eventInfo.getHttpProtocol().toLowerCase()
                    ));
                    if (eventInfo.isShouldDrop()) {
                        if (interceptedMessage != null) {
                            interceptedMessage.setInterceptAction(IInterceptedProxyMessage.ACTION_DROP);
                        } else {
                            sendToSelf(messageId, eventInfo, null);
                        }
                    }
                } else if (isRequest && eventInfo.getDataDirection() == DataDirection.Response) {
                    sendToSelf(messageId, eventInfo, interceptedMessage);
                } else {
                    messageInfo.setResponse(eventInfo.getHttpResponseMessage().getValue());
                }
            }
        } catch (Exception e) {
            Log.get().withMessage("Critical Error").withException(e).logErr();
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) {
                Log.get().withMessage(eventInfo.getDiagnostics().getLogs()).logRaw();
            }
        }
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

    private void sendToSelf(int messageId, IEventInfo eventInfo, IInterceptedProxyMessage interceptedMessage) {
        IHttpRequestResponse messageInfo = eventInfo.getRequestResponse();
        messageInfo.setRequest(BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                Stream.concat(Stream.of(
                        eventInfo.getHttpRequestMessage().getStatusLine().getValue(),
                        "Reshaper-ID: " + messageId
                        ),
                        eventInfo.getHttpRequestMessage().getHeaders().getValue().stream()
                ).collect(Collectors.toList()),
                eventInfo.getHttpRequestMessage().getBody().getValue()
        ));
        continuationMap.put(messageId, eventInfo);
        messageInfo.setHttpService(BurpExtender.getCallbacks().getHelpers().buildHttpService(
                serverSocket.getInetAddress().getHostAddress(),
                serverSocket.getLocalPort(),
                "http"
        ));
        if (interceptedMessage != null) {
            interceptedMessage.setInterceptAction(IInterceptedProxyMessage.ACTION_DONT_INTERCEPT);
        }
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        IEventInfo eventInfo = asEventInfo(messageIsRequest, message);
        processEvent(messageIsRequest, eventInfo, message);
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (toolFlag != BurpTool.Proxy.getId()) {
            BurpTool burpTool = getBurpToolIfEnabled(toolFlag);
            if (burpTool != null && burpTool != BurpTool.Proxy) {
                IEventInfo eventInfo = asEventInfo(messageIsRequest, burpTool, messageInfo);
                processEvent(messageIsRequest, eventInfo, null);
            }
        }
    }

    private BurpTool getBurpToolIfEnabled(int toolFlag) {
        GeneralSettings generalSettings = BurpExtender.getGeneralSettings();
        BurpTool burpTool = BurpTool.getById(toolFlag);
        return burpTool != null && generalSettings.isCapture(burpTool) ? burpTool : null;
    }
}
