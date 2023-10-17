package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HighlightColor;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.Url;
import synfron.reshaper.burp.core.utils.UrlUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class ThenSendTo extends Then<ThenSendTo> implements IHttpRuleOperation, IWebSocketRuleOperation {

    @Getter
    @Setter
    private SendToOption sendTo = SendToOption.Repeater;
    @Getter
    @Setter
    private boolean overrideDefaults;
    @Getter
    @Setter
    private VariableString host;
    @Getter
    @Setter
    private VariableString port;
    @Getter
    @Setter
    private VariableString protocol;
    @Getter
    @Setter
    private VariableString request;
    @Getter
    @Setter
    private VariableString response;
    @Getter
    @Setter
    private VariableString comment;
    @Getter
    @Setter
    private HighlightColor highlightColor = HighlightColor.None;
    @Getter
    @Setter
    private VariableString value;
    @Getter
    @Setter
    private VariableString url;

    public RuleResponse perform(EventInfo eventInfo) {
        switch (sendTo) {
            case Comparer -> sendToComparer(eventInfo);
            case Intruder -> sendToIntruder(eventInfo);
            case Repeater -> sendToRepeater(eventInfo);
            case Browser -> sendToBrowser(eventInfo);
            case Organizer -> sendToOrganizer(eventInfo);
            case Decoder -> sendToDecoder(eventInfo);
            case SiteMap -> sendToSiteMap(eventInfo);
        }
        return RuleResponse.Continue;
    }

    private void sendToSiteMap(EventInfo eventInfo) {
        String host = "";
        int port = 0;
        boolean isHttps = false;
        byte[] request = new byte[0];
        byte[] response = new byte[0];
        String comment = "";
        burp.api.montoya.core.HighlightColor highlightColor = burp.api.montoya.core.HighlightColor.NONE;
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                host = VariableString.getTextOrDefault(eventInfo, this.host, eventInfo.getDestinationAddress());
                port = VariableString.getIntOrDefault(eventInfo, this.port, eventInfo.getDestinationPort());
                isHttps = StringUtils.equalsIgnoreCase(
                        VariableString.getTextOrDefault(eventInfo, this.protocol, eventInfo.getHttpProtocol()),
                        "https"
                );
                request = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.request, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
                response = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.response, "")
                        ),
                        eventInfo instanceof HttpEventInfo httpEventInfo && (httpEventInfo.getInitialDataDirection() == HttpDataDirection.Response || httpEventInfo.getHttpResponseMessage().isSet()) ? httpEventInfo.getHttpResponseMessage().getValue() : new byte[0]
                );
                comment = VariableString.getTextOrDefault(eventInfo, this.comment, eventInfo.getAnnotations().notes());
                highlightColor = this.highlightColor.getHighlightColor();
            } else {
                host = eventInfo.getDestinationAddress();
                port = eventInfo.getDestinationPort();
                isHttps = StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https");
                request = eventInfo.getHttpRequestMessage().getValue();
                response = eventInfo instanceof HttpEventInfo httpEventInfo && (httpEventInfo.getInitialDataDirection() == HttpDataDirection.Response || httpEventInfo.getHttpResponseMessage().isSet()) ? httpEventInfo.getHttpResponseMessage().getValue() : new byte[0];
                comment = eventInfo.getAnnotations().notes();
                highlightColor = eventInfo.getAnnotations().highlightColor();
            }
            BurpExtender.getApi().siteMap().add(HttpRequestResponse.httpRequestResponse(HttpRequest.httpRequest(HttpService.httpService(host, port, isHttps), ByteArray.byteArray(request)), HttpResponse.httpResponse(ByteArray.byteArray(response))).withAnnotations(Annotations.annotations(comment, highlightColor)));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("host", host),
                        Pair.of("port", port),
                        Pair.of("isHttps", isHttps),
                        Pair.of("request", eventInfo.getEncoder().decode(request)),
                        Pair.of("response", eventInfo.getEncoder().decode(response)),
                        Pair.of("comment", comment),
                        Pair.of("highlightColor", highlightColor)
                ));
        }
    }

    private void sendToOrganizer(EventInfo eventInfo) {
        String host = "";
        int port = 0;
        boolean isHttps = false;
        byte[] request = new byte[0];
        byte[] response = new byte[0];
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                host = VariableString.getTextOrDefault(eventInfo, this.host, eventInfo.getDestinationAddress());
                port = VariableString.getIntOrDefault(eventInfo, this.port, eventInfo.getDestinationPort());
                isHttps = StringUtils.equalsIgnoreCase(
                        VariableString.getTextOrDefault(eventInfo, this.protocol, eventInfo.getHttpProtocol()),
                        "https"
                );
                request = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.request, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
                response = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.response, "")
                        ),
                        eventInfo instanceof HttpEventInfo httpEventInfo && (httpEventInfo.getInitialDataDirection() == HttpDataDirection.Response || httpEventInfo.getHttpResponseMessage().isSet()) ? httpEventInfo.getHttpResponseMessage().getValue() : new byte[0]
                );
            } else {
                host = eventInfo.getDestinationAddress();
                port = eventInfo.getDestinationPort();
                isHttps = StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https");
                request = eventInfo.getHttpRequestMessage().getValue();
                response = eventInfo instanceof HttpEventInfo httpEventInfo && (httpEventInfo.getInitialDataDirection() == HttpDataDirection.Response || httpEventInfo.getHttpResponseMessage().isSet()) ? httpEventInfo.getHttpResponseMessage().getValue() : new byte[0];
            }
            if (ArrayUtils.isNotEmpty(response)) {
                BurpExtender.getApi().organizer().sendToOrganizer(HttpRequestResponse.httpRequestResponse(HttpRequest.httpRequest(HttpService.httpService(host, port, isHttps), ByteArray.byteArray(request)), HttpResponse.httpResponse(ByteArray.byteArray(response))));
            } else {
                BurpExtender.getApi().organizer().sendToOrganizer(HttpRequest.httpRequest(HttpService.httpService(host, port, isHttps), ByteArray.byteArray(request)));
            }
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("host", host),
                        Pair.of("port", port),
                        Pair.of("isHttps", isHttps),
                        Pair.of("request", eventInfo.getEncoder().decode(request)),
                        Pair.of("response", response != null ? eventInfo.getEncoder().decode(response) : null)
                ));
        }
    }

    private void sendToIntruder(EventInfo eventInfo) {
        String host = "";
        int port = 0;
        boolean isHttps = false;
        byte[] request = new byte[0];
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                host = VariableString.getTextOrDefault(eventInfo, this.host, eventInfo.getDestinationAddress());
                port = VariableString.getIntOrDefault(eventInfo, this.port, eventInfo.getDestinationPort());
                isHttps = StringUtils.equalsIgnoreCase(
                        VariableString.getTextOrDefault(eventInfo, this.protocol, eventInfo.getHttpProtocol()),
                        "https"
                );
                request = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.request, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
            } else {
                host = eventInfo.getDestinationAddress();
                port = eventInfo.getDestinationPort();
                isHttps = StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https");
                request = eventInfo.getHttpRequestMessage().getValue();
            }
            BurpExtender.getApi().intruder().sendToIntruder(HttpRequest.httpRequest(HttpService.httpService(host, port, isHttps), ByteArray.byteArray(request)));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("host", host),
                        Pair.of("port", port),
                        Pair.of("isHttps", isHttps),
                        Pair.of("request", eventInfo.getEncoder().decode(request))
                ));
        }
    }

    private void sendToRepeater(EventInfo eventInfo) {
        String host = "";
        int port = 0;
        boolean isHttps = false;
        byte[] request = new byte[0];
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                host = VariableString.getTextOrDefault(eventInfo, this.host, eventInfo.getDestinationAddress());
                port = VariableString.getIntOrDefault(eventInfo, this.port, eventInfo.getDestinationPort());
                isHttps = StringUtils.equalsIgnoreCase(
                        VariableString.getTextOrDefault(eventInfo, this.protocol, eventInfo.getHttpProtocol()),
                        "https"
                );
                request = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, this.request, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
            } else {
                host = eventInfo.getDestinationAddress();
                port = eventInfo.getDestinationPort();
                isHttps = StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https");
                request = eventInfo.getHttpRequestMessage().getValue();
            }
            BurpExtender.getApi().repeater().sendToRepeater(HttpRequest.httpRequest(HttpService.httpService(host, port, isHttps), ByteArray.byteArray(request)));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("host", host),
                        Pair.of("port", port),
                        Pair.of("isHttps", isHttps),
                        Pair.of("request", eventInfo.getEncoder().decode(request))
                ));
        }
    }

    private void sendToDecoder(EventInfo eventInfo) {
        byte[] data = new byte[0];
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                data = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, value, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
            } else {
                data = eventInfo.getHttpRequestMessage().getValue();
            }
            BurpExtender.getApi().decoder().sendToDecoder(ByteArray.byteArray(data));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("data", eventInfo.getEncoder().decode(data))
                ));
        }
    }

    private void sendToComparer(EventInfo eventInfo) {
        byte[] data = new byte[0];
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                data = CollectionUtils.defaultIfEmpty(
                        eventInfo.getEncoder().encode(
                                VariableString.getTextOrDefault(eventInfo, value, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
            } else {
                data = eventInfo.getHttpRequestMessage().getValue();
            }
            BurpExtender.getApi().comparer().sendToComparer(ByteArray.byteArray(data));
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("data", eventInfo.getEncoder().decode(data))
                ));
        }
    }

    private void sendToBrowser(EventInfo eventInfo) {
        Url url = null;
        boolean hasError = false;
        try {
            if (overrideDefaults && this.url != null && !this.url.isEmpty()) {
                url = new Url(this.url.getText(eventInfo));
            } else {
                url = UrlUtils.getUrl(
                        eventInfo.getHttpProtocol().toLowerCase(),
                        eventInfo.getDestinationAddress(),
                        eventInfo.getDestinationPort(),
                        eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue()
                );
            }
            openBrowser(url.toURI());
        } catch (IOException e) {
            hasError = true;
            throw new WrappedException(e);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("sendTo", sendTo),
                        Pair.of("url", url)
                ));
        }
    }

    private void openBrowser(URI uri) throws IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(uri);
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Runtime.getRuntime().exec("start " + url);
        } else if (SystemUtils.IS_OS_MAC) {
            Runtime.getRuntime().exec(String.format("open \"%s\"", url));
        } else {
            Runtime.getRuntime().exec(String.format("xdg-open \"%1$s\" || sensible-browser \"%1$s\" || x-www-browser \"%1$s\"", url));
        }
    }

    @Override
    public RuleOperationType<ThenSendTo> getType() {
        return ThenType.SendTo;
    }
}
