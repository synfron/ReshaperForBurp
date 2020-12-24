package synfron.reshaper.burp.core.rules.thens.entities.script;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseStatusLine;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class XmlHttpRequestObj {
    public static final int UNSENT = 0;
    public static final int OPENED = 1;
    public static final int DONE = 4;
    private static final String requestTemplate = "%s %s HTTP/1.1\n" +
            "Host: %s\n" +
            "Accept: */*\n" +
            "Connection: close\n";
    private final Dispatcher dispatcher;
    @Getter
    private int readyState;
    private URI requestUrl;
    @Getter
    private String responseURL;
    @Getter @Setter
    private boolean withCredentials;
    @Getter @Setter
    private long timeout;
    @Getter @Setter
    private Function onreadystatechange;
    @Getter @Setter
    private Function ontimeout;
    @Getter @Setter
    private Function onload;
    @Getter @Setter
    private Function onerror;
    @Getter @Setter
    private Function onabort;
    private HttpRequestMessage requestMessage;
    private ScheduledExecutorService executor;
    private HttpResponseMessage responseMessage;

    public XmlHttpRequestObj() {
        dispatcher = Dispatcher.getCurrent();
    }

    public void open(String method, String url) throws URISyntaxException {
        open(method, url, true);
    }

    public void open(String method, String url, boolean async) throws URISyntaxException {
        if (!async) {
            throw new RuntimeException("Synchronous XHR not supported");
        }
        abort();
        URIBuilder inputUriBuilder = new URIBuilder(url);
        requestUrl = inputUriBuilder.build();
        URIBuilder uriBuilder = new URIBuilder("/");
        uriBuilder.setPath(StringUtils.defaultIfBlank(inputUriBuilder.getPath(), "/"));
        uriBuilder.setParameters(inputUriBuilder.getQueryParams());
        uriBuilder.setFragment(inputUriBuilder.getFragment());
        String request = String.format(requestTemplate, method, uriBuilder.toString(), requestUrl.getAuthority());
        requestMessage = new HttpRequestMessage(TextUtils.stringToBytes(request));
        setReadyState(OPENED);
    }

    private void resetResponse() {
        this.executor = null;
        this.responseURL = null;
        this.responseMessage = null;
    }

    public void abort() {
        ScheduledExecutorService executor = this.executor;
        resetResponse();
        setReadyState(UNSENT);
        if (executor != null) {
            executor.shutdownNow();
            execute(onabort);
        }
    }

    private void execute(Function func) {
        if (func != null) {
            func.call(
                    Context.getCurrentContext(),
                    func.getParentScope(),
                    !(func instanceof ArrowFunction) ?
                            new NativeJavaObject(func.getParentScope(), this, XmlHttpRequestObj.class) :
                            null,
                    null
            );
        }
    }

    private void setReadyState(int state) {
        if (readyState != state) {
            readyState = state;
            execute(onreadystatechange);
        }
    }

    public void send(Object... body) {
        resetResponse();
        executor = Executors.newSingleThreadScheduledExecutor();
        Dispatcher.Task timeoutTask = enableTimeout();
        doSend(TextUtils.toString(CollectionUtils.elementAtOrDefault(body, 0)), timeoutTask);
    }

    private Dispatcher.Task enableTimeout() {
        ScheduledExecutorService executor = this.executor;
        return dispatcher.execute(context -> {
            if (this.executor == executor) {
                executor.shutdownNow();
                this.executor = null;
                setReadyState(DONE);
                execute(ontimeout);
            }
        }, timeout > 0 ? timeout : Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    private void doSend(String body, Dispatcher.Task timeoutTask) {
        ScheduledExecutorService executor = this.executor;
        executor.execute(() -> {
            if (!Thread.interrupted()) {
                try {
                    HttpRequestMessage requestMessage = this.requestMessage;
                    if (StringUtils.isNotEmpty(body)) {
                        requestMessage = new HttpRequestMessage(requestMessage.getValue());
                        requestMessage.setBody(body);
                    }
                    boolean useHttps = !StringUtils.equalsIgnoreCase(requestUrl.getScheme(), "http");
                    byte[] response = BurpExtender.getCallbacks().makeHttpRequest(
                            requestUrl.getHost(),
                            requestUrl.getPort() > 0 ? requestUrl.getPort() : (useHttps ? 443 : 80),
                            useHttps,
                            requestMessage.getValue()
                    );
                    if (!dispatcher.isTimeoutReach() && this.executor == executor) {
                        this.executor = null;
                        dispatcher.execute(context -> {
                            if (response != null && response.length != 0) {
                                responseURL = requestUrl.toString();
                                responseMessage = new HttpResponseMessage(response);
                                setReadyState(DONE);
                                execute(onload);
                            } else {
                                setReadyState(DONE);
                                execute(onerror);
                            }
                        });
                    }
                } catch (Exception e) {
                    if (!dispatcher.isTimeoutReach() && this.executor == executor) {
                        dispatcher.execute(context -> {
                            setReadyState(DONE);
                            execute(onerror);
                        });
                    }
                }
                timeoutTask.cancel();
            }
        });
        executor.shutdown();
    }

    public int getStatus() {
        if (responseMessage != null) {
            String code = responseMessage.getStatusLine().getCode();
            return TextUtils.isInt(code) ? Integer.parseInt(code) : 0;
        }
        return 0;
    }

    public String getStatusText() {
        if (responseMessage != null) {
            HttpResponseStatusLine statusLine = responseMessage.getStatusLine();
            return String.format("%s %s", statusLine.getCode(), statusLine.getMessage());
        }
        return null;
    }

    public String getResponseText() {
        if (responseMessage != null) {
            return responseMessage.getBody().getText();
        }
        return null;
    }

    public String getResponseHeader(String name) {
        if (responseMessage != null) {
            return responseMessage.getHeaders().getHeader(name);
        }
        return null;
    }

    public String getAllResponseHeaders() {
        if (responseMessage != null) {
            return responseMessage.getHeaders().getText();
        }
        return null;
    }

    public void setRequestHeader(String name, String value) {
        requestMessage.getHeaders().setHeader(name, value);
    }
}
