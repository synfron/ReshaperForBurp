package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.sendto.SendToOption;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.VariableString;

import java.net.MalformedURLException;
import java.net.URL;

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
        if (overrideDefaults) {
            BurpExtender.getCallbacks().sendToIntruder(
                    VariableString.getTextOrDefault(eventInfo, host, eventInfo.getDestinationAddress()),
                    VariableString.getIntOrDefault(eventInfo, port, eventInfo.getDestinationPort()),
                    StringUtils.equalsIgnoreCase(
                            VariableString.getTextOrDefault(eventInfo, protocol, eventInfo.getHttpProtocol()),
                            "https"
                    ),
                    CollectionUtils.defaultIfEmpty(
                            TextUtils.stringToBytes(
                                    VariableString.getTextOrDefault(eventInfo, request, "")
                            ),
                            eventInfo.getHttpRequestMessage().getValue()
                    )
            );
        } else {
            BurpExtender.getCallbacks().sendToIntruder(
                    eventInfo.getDestinationAddress(),
                    eventInfo.getDestinationPort(),
                    StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https"),
                    eventInfo.getHttpRequestMessage().getValue()
            );
        }
    }

    private void sendToRepeater(EventInfo eventInfo) {
        if (overrideDefaults) {
            BurpExtender.getCallbacks().sendToRepeater(
                    VariableString.getTextOrDefault(eventInfo, host, eventInfo.getDestinationAddress()),
                    VariableString.getIntOrDefault(eventInfo, port, eventInfo.getDestinationPort()),
                    StringUtils.equalsIgnoreCase(
                            VariableString.getTextOrDefault(eventInfo, protocol, eventInfo.getHttpProtocol()),
                            "https"
                    ),
                    CollectionUtils.defaultIfEmpty(
                            TextUtils.stringToBytes(
                                    VariableString.getTextOrDefault(eventInfo, request, "")
                            ),
                            eventInfo.getHttpRequestMessage().getValue()
                    ),
                    null
            );
        } else {
            BurpExtender.getCallbacks().sendToRepeater(
                    eventInfo.getDestinationAddress(),
                    eventInfo.getDestinationPort(),
                    StringUtils.equalsIgnoreCase(eventInfo.getHttpProtocol(), "https"),
                    eventInfo.getHttpRequestMessage().getValue(),
                    null
            );
        }
    }

    private void sendToComparer(EventInfo eventInfo) {
        if (overrideDefaults) {
            BurpExtender.getCallbacks().sendToComparer(
                    CollectionUtils.defaultIfEmpty(
                            TextUtils.stringToBytes(
                                    VariableString.getTextOrDefault(eventInfo, value, "")
                            ),
                            eventInfo.getHttpRequestMessage().getValue()
                    )
            );
        } else {
            BurpExtender.getCallbacks().sendToComparer(eventInfo.getHttpRequestMessage().getValue());
        }
    }

    private void sendToSpider(EventInfo eventInfo) {
        try {
            if (overrideDefaults && url != null && !url.isEmpty()) {
                BurpExtender.getCallbacks().sendToSpider(
                        new URL(url.getText(eventInfo))
                );
            } else {
                BurpExtender.getCallbacks().sendToSpider(
                        new URL(
                                eventInfo.getHttpProtocol().toLowerCase(),
                                eventInfo.getDestinationAddress(),
                                eventInfo.getDestinationPort(),
                                eventInfo.getHttpRequestMessage().getStatusLine().getUrl().getValue()
                        )
                );
            }
        } catch (MalformedURLException e) {
            throw new WrappedException(e);
        }
    }

    @Override
    public RuleOperationType<ThenSendTo> getType() {
        return ThenType.SendTo;
    }
}
