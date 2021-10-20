package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.MessageValueHandler;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.rules.thens.entities.buildmessage.MessageSetter;
import synfron.reshaper.burp.core.vars.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ThenBuildMessage<T extends ThenBuildMessage<T>> extends Then<T> {
    @Getter @Setter
    private VariableString starterMessage;
    @Getter @Setter
    private List<MessageSetter> messageSetters = new ArrayList<>();
    @Getter @Setter
    private VariableSource variableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString variableName;

    public RuleResponse perform(EventInfo eventInfo) {
        try {
            Variables variables = switch (variableSource) {
                case Event -> eventInfo.getVariables();
                case Global -> GlobalVariables.get();
                default -> null;
            };
            if (variables != null) {
                Variable variable = variables.add(variableName.getText(eventInfo));
                variable.setValue(buildMessage(eventInfo));
            }
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logProperties(this, false, Stream.concat(
                    Stream.of(
                            Pair.of("starterMessage", VariableString.getTextOrDefault(eventInfo, starterMessage, null)),
                            Pair.of("variableSource", variableSource.toString()),
                            Pair.of("variableName", VariableString.getTextOrDefault(eventInfo, variableName, null))
                    ),
                    messageSetters.stream().map(messageSetter -> Pair.of(
                            messageSetter.getMessageValue() + (MessageValueHandler.hasIdentifier(messageSetter.getMessageValue()) ? messageSetter.getIdentifier().getText(eventInfo) : ""), messageSetter.getText()))
            ).collect(Collectors.toList()));
        } catch (Exception e) {
            if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, true, Collections.emptyList());
        }
        return RuleResponse.Continue;
    }

    protected abstract String buildMessage(EventInfo eventInfo);
}
