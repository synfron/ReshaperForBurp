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
import synfron.reshaper.burp.core.rules.thens.entities.parsehttpmessage.MessageValueGetter;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThenParseHttpMessage extends Then<ThenParseHttpMessage> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private HttpDataDirection dataDirection = HttpDataDirection.Request;
    @Getter @Setter
    private VariableString httpMessage;
    @Getter @Setter
    private List<MessageValueGetter> messageValueGetters = new ArrayList<>();

    public RuleResponse perform(EventInfo eventInfo) {
        try {
            List<Pair<String, String>> variables;
            if (dataDirection == HttpDataDirection.Request) {
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
            throw e;
        }
        return RuleResponse.Continue;
    }

    private List<Pair<String, String>> parseRequestMessage(EventInfo eventInfo) {
        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(eventInfo.getEncoder().encode(
                VariableString.getTextOrDefault(eventInfo, httpMessage, "")
        ), eventInfo.getEncoder());
        List<Pair<String, String>> variables = new ArrayList<>();
        for (MessageValueGetter messageValueGetter : getMessageValueGetters()) {
            String variableName = messageValueGetter.getDestinationVariableName().getText(eventInfo);
            String delimiter = VariableString.getTextOrDefault(eventInfo, messageValueGetter.getDelimiter(), "\n");
            Integer index = VariableString.getIntOrDefault(eventInfo, messageValueGetter.getIndex(), 0);
            String value = MessageValueHandler.getRequestValue(
                    eventInfo,
                    httpRequestMessage,
                    messageValueGetter.getSourceMessageValue(),
                    messageValueGetter.getSourceIdentifier(),
                    messageValueGetter.getSourceIdentifierPlacement()
            );
            variables.add(setVariable(
                    eventInfo,
                    messageValueGetter.getDestinationVariableSource(),
                    variableName,
                    messageValueGetter.getItemPlacement(),
                    delimiter,
                    index,
                    value
            ));
        }
        return variables;
    }

    private List<Pair<String, String>> parseResponseMessage(EventInfo eventInfo) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(eventInfo.getEncoder().encode(
                VariableString.getTextOrDefault(eventInfo, httpMessage, "")
        ), eventInfo.getEncoder());
        List<Pair<String, String>> variables = new ArrayList<>();
        for (MessageValueGetter messageValueGetter : getMessageValueGetters()) {
            String variableName = messageValueGetter.getDestinationVariableName().getText(eventInfo);
            String delimiter = VariableString.getTextOrDefault(eventInfo, messageValueGetter.getDelimiter(), "\n");
            Integer index = VariableString.getIntOrDefault(eventInfo, messageValueGetter.getIndex(), 0);
            String value = MessageValueHandler.getResponseValue(
                    eventInfo,
                    httpResponseMessage,
                    messageValueGetter.getSourceMessageValue(),
                    messageValueGetter.getSourceIdentifier(),
                    messageValueGetter.getSourceIdentifierPlacement()
            );
            variables.add(setVariable(
                    eventInfo,
                    messageValueGetter.getDestinationVariableSource(),
                    variableName,
                    messageValueGetter.getItemPlacement(),
                    delimiter,
                    index,
                    value
            ));
        }
        return variables;
    }

    private Pair<String, String> setVariable(EventInfo eventInfo, VariableSource variableSource, String variableName, SetListItemPlacement itemPlacement, String delimiter, Integer index, String value) {
        super.setVariable(variableSource, eventInfo, variableName, itemPlacement, delimiter, index, value);
        return variableSource.isList() ?
                Pair.of(VariableTag.getTag(
                        variableSource,
                        variableName,
                        itemPlacement.toString(),
                        itemPlacement.isHasIndexSetter() ?
                                TextUtils.toString(index) : null
                ), value) :
                Pair.of(VariableTag.getTag(variableSource, variableName), value);
    }

    @Override
    public RuleOperationType<ThenParseHttpMessage> getType() {
        return ThenType.ParseHttpMessage;
    }
}
