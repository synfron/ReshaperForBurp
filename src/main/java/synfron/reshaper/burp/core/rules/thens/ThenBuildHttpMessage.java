package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpDataDirection;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.messages.entities.http.HttpRequestMessage;
import synfron.reshaper.burp.core.messages.entities.http.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.buildhttpmessage.MessageValueSetter;
import synfron.reshaper.burp.core.vars.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenBuildHttpMessage extends Then<ThenBuildHttpMessage> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private HttpDataDirection dataDirection = HttpDataDirection.Request;
    @Getter @Setter
    private VariableString starterHttpMessage;
    @Getter @Setter
    private List<MessageValueSetter> messageValueSetters = new ArrayList<>();
    @Getter @Setter
    private VariableSource destinationVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString destinationVariableName;
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    public RuleResponse perform(EventInfo eventInfo) {
        try {
            String value = dataDirection == HttpDataDirection.Request ? buildRequestMessage(eventInfo) : buildResponseMessage(eventInfo);
            Variables variables = getVariables(destinationVariableSource, eventInfo);
            if (variables != null) {
                Variable variable = variables.add(Variables.asKey(destinationVariableName.getText(eventInfo), destinationVariableSource.isList()));
                variable.setValue(itemPlacement, VariableString.getTextOrDefault(eventInfo, delimiter, "\n"), VariableString.getIntOrDefault(eventInfo, index, 0), value);
            }
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Stream.concat(
                    Stream.of(
                            Pair.of("dataDirection", dataDirection.toString()),
                            Pair.of("starterHttpMessage", VariableString.getTextOrDefault(eventInfo, starterHttpMessage, null)),
                            Pair.of("destinationVariableSource", destinationVariableSource.toString()),
                            Pair.of("destinationVariableName", VariableString.getTextOrDefault(eventInfo, destinationVariableName, null)),
                            Pair.of("itemPlacement", destinationVariableSource.isList() ? itemPlacement : null),
                            Pair.of("delimiter", destinationVariableSource.isList() && itemPlacement.isHasDelimiterSetter() ? VariableString.getTextOrDefault(eventInfo, delimiter, null) : null),
                            Pair.of("index", destinationVariableSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null)
                    ),
                    messageValueSetters.stream().map(messageValueSetter -> Pair.of(
                            VariableSourceEntry.getTag(
                                    VariableSource.Message,
                                    messageValueSetter.getDestinationMessageValue().name().toLowerCase(),
                                    messageValueSetter.getDestinationMessageValue().isIdentifierRequired() ? VariableString.getTextOrDefault(
                                            eventInfo, messageValueSetter.getDestinationIdentifier(), null
                                    ) : ""
                            ),
                            messageValueSetter.getSourceText().getText(eventInfo)
                    ))
            ).collect(Collectors.toList()));
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
            throw e;
        }
        return RuleResponse.Continue;
    }

    private String buildRequestMessage(EventInfo eventInfo) {
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(eventInfo.getEncoder().encode(
                VariableString.getTextOrDefault(eventInfo, starterHttpMessage, "")
        ), eventInfo.getEncoder());
        for (MessageValueSetter messageValueSetter : getMessageValueSetters()) {
            MessageValueHandler.setRequestValue(
                    eventInfo,
                    httpRequestMessage,
                    messageValueSetter.getDestinationMessageValue(),
                    messageValueSetter.getDestinationIdentifier(),
                    messageValueSetter.getDestinationIdentifierPlacement(),
                    messageValueSetter.getSourceText().getText(eventInfo)
            );
        }
        return httpRequestMessage.getText();
    }

    private String buildResponseMessage(EventInfo eventInfo) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(eventInfo.getEncoder().encode(
                VariableString.getTextOrDefault(eventInfo, starterHttpMessage, "")
        ), eventInfo.getEncoder());
        for (MessageValueSetter messageValueSetter : getMessageValueSetters()) {
            MessageValueHandler.setResponseValue(
                    eventInfo,
                    httpResponseMessage,
                    messageValueSetter.getDestinationMessageValue(),
                    messageValueSetter.getDestinationIdentifier(),
                    messageValueSetter.getDestinationIdentifierPlacement(),
                    messageValueSetter.getSourceText().getText(eventInfo)
            );
        }
        return httpResponseMessage.toString();
    }

    @Override
    public RuleOperationType<ThenBuildHttpMessage> getType() {
        return ThenType.BuildHttpMessage;
    }
}
