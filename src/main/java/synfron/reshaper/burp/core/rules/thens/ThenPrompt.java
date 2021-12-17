package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.events.MessageArgs;
import synfron.reshaper.burp.core.events.message.*;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ThenPrompt extends Then<ThenPrompt> {
    @Getter @Setter
    private VariableString description;
    @Getter @Setter
    private VariableString failAfter;
    @Getter @Setter
    private boolean breakAfterFailure = true;
    @Getter @Setter
    private VariableSource captureVariableSource = VariableSource.Global;
    @Getter @Setter
    private VariableString captureVariableName;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        boolean hasError = false;
        boolean failed = false;
        boolean complete = false;
        String output = null;
        String captureVariableName = null;
        String description = null;
        try {
            int failAfterInMilliseconds = this.failAfter.getInt(eventInfo);
            captureVariableName = this.captureVariableName.getText(eventInfo);
            description = this.description.getText(eventInfo);
            PromptRequestMessage requestMessage = new PromptRequestMessage(UUID.randomUUID().toString(), description);
            MessageWaiter<PromptResponseMessage> messageWaiter = new MessageWaiter<>(
                    BurpExtender.getMessageEvent(),
                    MessageType.PromptResponse,
                    responseMessage -> responseMessage.getMessageId().equals(requestMessage.getMessageId())
            );
            BurpExtender.getMessageEvent().invoke(new MessageArgs(this, requestMessage));
            if (messageWaiter.waitForMessage(failAfterInMilliseconds, TimeUnit.MILLISECONDS)) {
                output = messageWaiter.getMessage().getResponse();
                if (output != null) {
                    setVariable(eventInfo, captureVariableName, output);
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
                        Pair.of("output", output),
                        Pair.of("captureVariableSource", captureVariableSource),
                        Pair.of("captureVariableName", captureVariableName),
                        Pair.of("exceededWait", !complete),
                        Pair.of("failed", failed)
                ));
        }
        return failed && breakAfterFailure ? RuleResponse.BreakRules : RuleResponse.Continue;
    }

    private void setVariable(EventInfo eventInfo, String variableName, String value) {
        Variables variables = switch (captureVariableSource) {
            case Event -> eventInfo.getVariables();
            case Global -> GlobalVariables.get();
            default -> null;
        };
        if (variables != null) {
            Variable variable = variables.add(variableName);
            variable.setValue(value);
        }
    }

    @Override
    public RuleOperationType<ThenPrompt> getType() {
        return ThenType.Prompt;
    }
}
