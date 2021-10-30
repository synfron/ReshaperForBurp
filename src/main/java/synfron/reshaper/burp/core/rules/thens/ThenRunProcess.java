package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThenRunProcess extends Then<ThenRunProcess> {
    @Getter @Setter
    private VariableString command;
    @Getter @Setter
    private VariableString input;
    @Getter @Setter
    private boolean waitForCompletion;
    @Getter @Setter
    private VariableString failAfter;
    @Getter @Setter
    private boolean killAfterFailure;
    @Getter @Setter
    private boolean failOnNonZeroExitCode;
    @Getter @Setter
    private boolean breakAfterFailure = true;
    @Getter @Setter
    private boolean captureOutput;
    @Getter @Setter
    private boolean captureAfterFailure;
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
        Integer exitCode = null;
        String input;
        String captureVariableName = null;
        StringBuilderWriter stringWriter = new StringBuilderWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
        try {
            int failAfterInSeconds = waitForCompletion ? getFailAfter(eventInfo) : 0;
            input = VariableString.getTextOrDefault(eventInfo, this.input, "");
            captureVariableName = getVariableName(eventInfo);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Process process = Runtime.getRuntime().exec(command.getText(eventInfo));
            if (StringUtils.isNotEmpty(input)) {
                IOUtils.write(input, process.getOutputStream(), Charset.defaultCharset());
                process.getOutputStream().close();
            }
            if (waitForCompletion) {
                executor.submit(() -> {
                    try {
                        if (captureOutput) {
                            IOUtils.copy(process.getInputStream(), bufferedWriter, Charset.defaultCharset());
                        }
                        process.waitFor();
                    } catch (InterruptedException | IOException ignored) {}
                    finally {
                        executor.shutdown();
                    }
                });
                complete = executor.awaitTermination(failAfterInSeconds, TimeUnit.MILLISECONDS);
                if (!complete) {
                    if (killAfterFailure && process.isAlive()) {
                        try {
                            process.destroyForcibly().waitFor(10, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            Log.get().withMessage("Problem encounter killing process after failure.").withException(e).logErr();
                        }
                    }
                    executor.shutdownNow();
                }
                exitCode = complete || !process.isAlive() ? process.exitValue() : null;
                failed = !complete || (failOnNonZeroExitCode && exitCode != 0);
                if (captureOutput && (!failed || captureAfterFailure)) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    output = stringWriter.toString();
                    setVariable(eventInfo, captureVariableName, output);
                }
            }

        } catch (InterruptedException | IOException e) {
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
                        Pair.of("command", VariableString.getTextOrDefault(eventInfo, command, null)),
                        Pair.of("output", output),
                        Pair.of("captureVariableSource", waitForCompletion && captureOutput ? captureVariableSource : null),
                        Pair.of("captureVariableName", waitForCompletion && captureOutput ? captureVariableName : null),
                        Pair.of("exceededWait", waitForCompletion ? !complete : null),
                        Pair.of("failed", waitForCompletion ? failed : null),
                        Pair.of("exitCode", waitForCompletion ? exitCode : null)
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

    private String getVariableName(EventInfo eventInfo) {
        String captureVariableName = null;
        if (captureOutput && StringUtils.isEmpty(captureVariableName = this.captureVariableName.getText(eventInfo))) {
            throw new IllegalArgumentException("Invalid variable name");
        }
        return captureVariableName;
    }

    private int getFailAfter(EventInfo eventInfo) {
        int failAfter = 0;
        if (waitForCompletion && (failAfter = this.failAfter.getInt(eventInfo)) <= 0) {
            throw new IllegalArgumentException("Invalid wait limit value");
        }
        return failAfter;
    }

    @Override
    public RuleOperationType<ThenRunProcess> getType() {
        return ThenType.RunProcess;
    }
}
