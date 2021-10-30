package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.DataDirection;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.entities.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.vars.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenBuildHttpMessage extends Then<ThenBuildHttpMessage> {
    @Getter @Setter
    private DataDirection dataDirection = DataDirection.Request;
    @Getter @Setter
    private VariableString starterHttpMessage;
    @Getter @Setter
    private List<MessageValueSetter> messageValueSetters = new ArrayList<>();
    @Getter @Setter
    private VariableSource destinationVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString destinationVariableName;

    public RuleResponse perform(EventInfo eventInfo) {
        try {
            Variables variables = switch (destinationVariableSource) {
                case Event -> eventInfo.getVariables();
                case Global -> GlobalVariables.get();
                default -> null;
            };
            if (variables != null) {
                Variable variable = variables.add(destinationVariableName.getText(eventInfo));
                variable.setValue(dataDirection == DataDirection.Request ? buildRequestMessage(eventInfo) : buildResponseMessage(eventInfo));
            }
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Stream.concat(
                    Stream.of(
                            Pair.of("dataDirection", dataDirection.toString()),
                            Pair.of("starterHttpMessage", VariableString.getTextOrDefault(eventInfo, starterHttpMessage, null)),
                            Pair.of("destinationVariableSource", destinationVariableSource.toString()),
                            Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null))
                    ),
                    messageValueSetters.stream().map(messageValueSetter -> Pair.of(
                            VariableString.getFormattedString(
                                    messageValueSetter.getDestinationMessageValue(), VariableString.getTextOrDefault(
                                            eventInfo, messageValueSetter.getDestinationIdentifier(), null
                                    )),
                            messageValueSetter.getSourceText().getText(eventInfo)
                    ))
            ).collect(Collectors.toList()));
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
        }
        return RuleResponse.Continue;
    }

    private String buildRequestMessage(EventInfo eventInfo) {
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, starterHttpMessage, "")
        ));
        for (MessageValueSetter messageValueSetter : getMessageValueSetters()) {
            MessageValueHandler.setRequestValue(eventInfo, httpRequestMessage, messageValueSetter.getDestinationMessageValue(), messageValueSetter.getDestinationIdentifier(), messageValueSetter.getSourceText().getText(eventInfo));
        }
        return httpRequestMessage.getText();
    }

    private String buildResponseMessage(EventInfo eventInfo) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, starterHttpMessage, "")
        ));
        for (MessageValueSetter messageValueSetter : getMessageValueSetters()) {
            MessageValueHandler.setResponseValue(eventInfo, httpResponseMessage, messageValueSetter.getDestinationMessageValue(), messageValueSetter.getDestinationIdentifier(), messageValueSetter.getSourceText().getText(eventInfo));
        }
        return httpResponseMessage.toString();
    }

    @Override
    public RuleOperationType<ThenBuildHttpMessage> getType() {
        return ThenType.BuildHttpMessage;
    }
}
