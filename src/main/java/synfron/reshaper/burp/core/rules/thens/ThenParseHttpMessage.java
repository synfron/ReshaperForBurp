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
import synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage.MessageValueGetter;
import synfron.reshaper.burp.core.vars.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenParseHttpMessage extends Then<ThenParseHttpMessage> {
    @Getter @Setter
    private DataDirection dataDirection = DataDirection.Request;
    @Getter @Setter
    private VariableString httpMessage;
    @Getter @Setter
    private List<MessageValueGetter> messageValueGetters = new ArrayList<>();

    public RuleResponse perform(EventInfo eventInfo) {
        try {
            List<Pair<String, String>> variables;
            if (dataDirection == DataDirection.Request) {
                variables = parseRequestMessage(eventInfo);
            } else {
                variables = parseResponseMessage(eventInfo);
            }
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Stream.concat(
                    Stream.of(
                            Pair.of("dataDirection", dataDirection.toString()),
                            Pair.of("httpMessage", VariableString.getTextOrDefault(eventInfo, httpMessage, null))
                    ),
                    variables.stream()

            ).collect(Collectors.toList()));
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
        }
        return RuleResponse.Continue;
    }

    private List<Pair<String, String>> parseRequestMessage(EventInfo eventInfo) {
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, httpMessage, "")
        ));
        List<Pair<String, String>> variables = new ArrayList<>();
        for (MessageValueGetter messageValueGetter : getMessageValueGetters()) {
            String variableName = messageValueGetter.getDestinationVariableName().getText(eventInfo);
            String value = MessageValueHandler.getRequestValue(eventInfo, httpRequestMessage, messageValueGetter.getSourceMessageValue(), messageValueGetter.getSourceIdentifier());
            variables.add(setVariable(eventInfo, messageValueGetter.getDestinationVariableSource(), variableName, value));
        }
        return variables;
    }

    private List<Pair<String, String>> parseResponseMessage(EventInfo eventInfo) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(BurpExtender.getCallbacks().getHelpers().stringToBytes(
                VariableString.getTextOrDefault(eventInfo, httpMessage, "")
        ));
        List<Pair<String, String>> variables = new ArrayList<>();
        for (MessageValueGetter messageValueGetter : getMessageValueGetters()) {
            String variableName = messageValueGetter.getDestinationVariableName().getText(eventInfo);
            String value = MessageValueHandler.getResponseValue(eventInfo, httpResponseMessage, messageValueGetter.getSourceMessageValue(), messageValueGetter.getSourceIdentifier());
            variables.add(setVariable(eventInfo, messageValueGetter.getDestinationVariableSource(), variableName, value));
        }
        return variables;
    }

    private Pair<String, String> setVariable(EventInfo eventInfo, VariableSource variableSource, String variableName, String value) {
        Variables variables = switch (variableSource) {
            case Event -> eventInfo.getVariables();
            case Global -> GlobalVariables.get();
            default -> null;
        };
        Variable variable;
        if (variables != null) {
            variable = variables.add(variableName);
            variable.setValue(value);
        }
        return Pair.of(VariableString.getTag(variableSource, variableName), value);
    }

    @Override
    public RuleOperationType<ThenParseHttpMessage> getType() {
        return ThenType.ParseHttpMessage;
    }
}
