package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.ObjectUtils;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ThenSendTo extends Then<ThenSendTo> {

    @Getter @Setter
    private SendToOption sendTo = SendToOption.Repeater;
    @Getter @Setter
    private boolean overrideDefaults;
    @Getter @Setter
    private VariableString host;
    @Getter @Setter
    private VariableString port;
    @Getter @Setter
    private VariableString protocol;
    @Getter @Setter
    private VariableString request;
    @Getter @Setter
    private VariableString value;
    @Getter @Setter
    private VariableString url;

    public RuleResponse perform(EventInfo eventInfo)
    {
        boolean hasError = false;
        switch (sendTo) {
            case Comparer:
                sendToComparer(eventInfo);
                break;
            case Intruder:
                sendToIntruder(eventInfo);
                break;
            case Repeater:
                sendToRepeater(eventInfo);
                break;
            case Spider:
                sendToSpider(eventInfo);
                break;
        }
        return RuleResponse.Continue;
    }

    private void sendToIntruder(EventInfo eventInfo) {
        String host = null;
        int port = 0;
        boolean isHttps = false;
        byte[] request = null;
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
                        TextUtils.stringToBytes(
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
            BurpExtender.getCallbacks().sendToIntruder(host, port, isHttps, request);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("sendTo", sendTo),
                    Pair.of("host", host),
                    Pair.of("port", port),
                    Pair.of("isHttps", isHttps),
                    Pair.of("request", TextUtils.bytesToString(request))
            ));
        }
    }

    private void sendToRepeater(EventInfo eventInfo) {
        String host = null;
        int port = 0;
        boolean isHttps = false;
        byte[] request = null;
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
                        TextUtils.stringToBytes(
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
            BurpExtender.getCallbacks().sendToRepeater(host, port, isHttps, request, null);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("sendTo", sendTo),
                    Pair.of("host", host),
                    Pair.of("port", port),
                    Pair.of("isHttps", isHttps),
                    Pair.of("request", TextUtils.bytesToString(request))
            ));
        }
    }

    private void sendToComparer(EventInfo eventInfo) {
        byte[] data = null;
        boolean hasError = false;
        try {
            if (overrideDefaults) {
                data = CollectionUtils.defaultIfEmpty(
                        TextUtils.stringToBytes(
                                VariableString.getTextOrDefault(eventInfo, value, "")
                        ),
                        eventInfo.getHttpRequestMessage().getValue()
                );
            } else {
                data = eventInfo.getHttpRequestMessage().getValue();
            }
            BurpExtender.getCallbacks().sendToComparer(data);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("sendTo", sendTo),
                    Pair.of("data", TextUtils.bytesToString(data))
            ));
        }
    }

    private void sendToSpider(EventInfo eventInfo) {
        URL url = null;
        boolean hasError = false;
        try {
            if (overrideDefaults && this.url != null && !this.url.isEmpty()) {
                url = new URL(this.url.getText(eventInfo));
            } else {
                url = ObjectUtils.getUrl(
                        eventInfo.getHttpProtocol().toLowerCase(),
                        eventInfo.getDestinationAddress(),
                        eventInfo.getDestinationPort(),
                        eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue()
                );
            }
            BurpExtender.getCallbacks().sendToSpider(url);
        } catch (MalformedURLException e) {
            hasError = true;
            throw new WrappedException(e);
        } catch (Exception e) {
            hasError = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                    Pair.of("sendTo", sendTo),
                    Pair.of("url", url)
            ));
        }
    }

    @Override
    public RuleOperationType<ThenSendTo> getType() {
        return ThenType.SendTo;
    }
}
