package synfron.reshaper.burp.core;

import burp.*;
import lombok.Getter;
import net.jodah.expiringmap.ExpiringMap;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RulesEngine;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.GlobalVariables;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Connector implements IProxyListener, IExtensionStateListener {

    @Getter
    private final RulesEngine rulesEngine = new RulesEngine();
    private ServerSocket serverSocket;
    private final Map<Integer, EventInfo> continuationMap = ExpiringMap.builder()
            .expiration(5, TimeUnit.MINUTES).build();
    private boolean activated = true;

    public void init() {
        activated = true;
        GlobalVariables.get().load();
        rulesEngine.init();
        createServer();
    }

    @Override
    public void extensionUnloaded() {
        activated = false;
        GlobalVariables.get().save();
        rulesEngine.save();
        Log.get().withMessage("Reshaper unloaded").log();
    }

    private void processServerConnection(Socket socket) {
        new Thread(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedReader.readLine();
                int reshaperId = getReshaperId(bufferedReader.readLine());
                EventInfo eventInfo = continuationMap.remove(reshaperId);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write(eventInfo.getHttpResponseMessage().getValue());
                bufferedOutputStream.flush();
            } catch (Exception e) {
                Log.get().withMessage("Event processing failed").withException(e).logErr();
            } finally {
                close(socket);
            }
        }).start();
    }

    private void createServer() {
        try {
            serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
            new Thread(() -> {
                while (activated) {
                    try {
                        Socket socket = serverSocket.accept();
                        processServerConnection(socket);
                    } catch (Exception e) {
                        Log.get().withMessage("Server accept new connection failed").withException(e).logErr();
                    }
                }
            }).start();
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
            Log.get().withMessage("Failed closing stream").withException(ex).logErr();
        }
    }

    private EventInfo asEventInfo(boolean messageIsRequest, IInterceptedProxyMessage message) {
        return new EventInfo(messageIsRequest ? DataDirection.Request : DataDirection.Response, message);
    }

    private int getReshaperId(String header) {
        if (!header.startsWith("Reshaper-ID:")) {
            throw new InputMismatchException("No Reshaper-ID found");
        }
        return Integer.parseInt(header.split(":", 2)[1].trim());
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        try {
            EventInfo eventInfo = asEventInfo(messageIsRequest, message);
            rulesEngine.run(eventInfo);
            if (eventInfo.isChanged()) {
                IHttpRequestResponse messageInfo = message.getMessageInfo();
                if (eventInfo.getDataDirection() == DataDirection.Request) {
                    messageInfo.setRequest(eventInfo.getHttpRequestMessage().getValue());
                    messageInfo.setHttpService(BurpExtender.getCallbacks().getHelpers().buildHttpService(
                            eventInfo.getDestinationAddress(),
                            eventInfo.getDestinationPort(),
                            eventInfo.getHttpProtocol().toLowerCase()
                    ));
                } else if (messageIsRequest && eventInfo.getDataDirection() == DataDirection.Response) {
                    messageInfo.setRequest(BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                            Stream.concat(Stream.of(
                                    eventInfo.getHttpRequestMessage().getStatusLine().getValue(),
                                    "Reshaper-ID: " + message.getMessageReference()
                                    ),
                                    eventInfo.getHttpRequestMessage().getHeaders().getValue().stream()
                            ).collect(Collectors.toList()),
                            eventInfo.getHttpRequestMessage().getBody().getValue()
                    ));
                    continuationMap.put(message.getMessageReference(), eventInfo);
                    messageInfo.setHttpService(BurpExtender.getCallbacks().getHelpers().buildHttpService(
                            serverSocket.getInetAddress().getHostAddress(),
                            serverSocket.getLocalPort(),
                            "http"
                    ));
                    message.setInterceptAction(IInterceptedProxyMessage.ACTION_DONT_INTERCEPT);
                } else {
                    messageInfo.setResponse(eventInfo.getHttpResponseMessage().getValue());
                }
            }
            if (!messageIsRequest) {
                continuationMap.remove(message.getMessageReference());
            }
        } catch (Exception e) {
            Log.get().withMessage("Critical Error").withException(e).logErr();
        }
    }
}
