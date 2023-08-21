package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.message.*;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.vars.SetListItemPlacement;
import synfron.reshaper.burp.core.vars.VariableSource;
import synfron.reshaper.burp.core.vars.VariableString;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ThenPrompt extends Then<ThenPrompt> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private VariableString description;
    @Getter @Setter
    private VariableString starterText;
    @Getter @Setter
    private VariableString failAfter;
    @Getter @Setter
    private boolean breakAfterFailure = true;
    @Getter @Setter
    private VariableSource captureVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString captureVariableName;
    @Getter @Setter
    private SetListItemPlacement itemPlacement = SetListItemPlacement.Index;
    @Getter @Setter
    private VariableString delimiter;
    @Getter @Setter
    private VariableString index;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        boolean failed = false;
        boolean complete = false;
        String output = null;
        String captureVariableName = null;
        String description = null;
        String starterText = null;
        try {
            int failAfterInMilliseconds = this.failAfter.getInt(eventInfo);
            captureVariableName = this.captureVariableName.getText(eventInfo);
            description = this.description.getText(eventInfo);
            starterText = VariableString.getTextOrDefault(eventInfo, this.starterText, "");
            PromptRequestMessage requestMessage = new PromptRequestMessage(UUID.randomUUID().toString(), description, starterText);
            MessageWaiter<PromptResponseMessage> messageWaiter = new MessageWaiter<>(
                    BurpExtender.getMessageEvent(),
                    MessageType.PromptResponse,
                    responseMessage -> responseMessage.getMessageId().equals(requestMessage.getMessageId())
            );
            BurpExtender.getMessageEvent().invoke(new MessageArgs(this, requestMessage));
            if (messageWaiter.waitForMessage(failAfterInMilliseconds, TimeUnit.MILLISECONDS)) {
                output = messageWaiter.getMessage().getResponse();
                if (output != null) {
                    setVariable(
                            captureVariableSource,
                            eventInfo,
                            captureVariableName,
                            itemPlacement,
                            VariableString.getTextOrDefault(eventInfo, delimiter, "\n"),
                            VariableString.getIntOrDefault(eventInfo, index, 0),
                            output
                    );
                } else {
                    failed = true;
                }
                complete = true;
            } else {
                BurpExtender.getMessageEvent().invoke(new MessageArgs(this, new PromptCancelMessage(requestMessage.getMessageId())));
                failed = true;
            }
        } catch (InterruptedException e) {
            hasError = true;
            complete = true;
            throw new WrappedException(e);
        } catch (Exception e) {
            hasError = true;
            complete = true;
            throw e;
        } finally {
            if (eventInfo.getDiagnostics().isEnabled())
                eventInfo.getDiagnostics().logProperties(this, hasError, Arrays.asList(
                        Pair.of("description", description),
                        Pair.of("starterText", starterText),
                        Pair.of("output", output),
                        Pair.of("captureVariableSource", captureVariableSource),
                        Pair.of("captureVariableName", captureVariableName),
                        Pair.of("itemPlacement", captureVariableSource.isList() ? itemPlacement : null),
                        Pair.of("delimiter", captureVariableSource.isList() && itemPlacement.isHasDelimiterSetter() ? VariableString.getTextOrDefault(eventInfo, delimiter, null) : null),
                        Pair.of("index", captureVariableSource.isList() && itemPlacement.isHasIndexSetter() ? VariableString.getTextOrDefault(eventInfo, index, null) : null),
                        Pair.of("exceededWait", !complete),
                        Pair.of("failed", failed)
                ));
        }
        return failed && breakAfterFailure ? RuleResponse.BreakRules : RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenPrompt> getType() {
        return ThenType.Prompt;
    }
}
