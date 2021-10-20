package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ThenSendRequest extends Then<ThenSendRequest> {
    @Getter @Setter
    private VariableString protocol;
    @Getter @Setter
    private VariableString address;
    @Getter @Setter
    private VariableString port;
    @Getter @Setter
    private VariableString request;
    @Getter @Setter
    private boolean waitForCompletion;
    @Getter @Setter
    private VariableString failAfter;
    @Getter @Setter
    private boolean failOnErrorStatusCode;
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
        int exitCode = 0;
        String captureVariableName = null;
        try {
            int failAfterInSeconds = waitForCompletion ? getFailAfter(eventInfo) : 0;
            captureVariableName = getVariableName(eventInfo);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AtomicReference<byte[]> response = new AtomicReference<>();
                executor.submit(() -> {
                    try {
                        boolean useHttps = !StringUtils.equalsIgnoreCase(VariableString.getTextOrDefault(eventInfo, protocol, eventInfo.getHttpProtocol()), "http");
                        String address = VariableString.getTextOrDefault(eventInfo, this.address, eventInfo.getDestinationAddress());
                        int port = VariableString.getIntOrDefault(eventInfo, this.port, eventInfo.getDestinationPort() == null ? 0 : eventInfo.getDestinationPort());
                        byte[] request = this.request != null && !this.request.isEmpty() ?
                                BurpExtender.getCallbacks().getHelpers().stringToBytes(this.request.getText(eventInfo)) :
                                eventInfo.getHttpRequestMessage().getValue();
                        byte[] responseBytes = BurpExtender.getCallbacks().makeHttpRequest(
                                address,
                                port > 0 ? port : (useHttps ? 443 : 80),
                                useHttps,
                                request
                        );
                        response.set(responseBytes);
                    }
                    catch (Exception e) {
                        Log.get().withMessage("Failure sending request").withException(e).logErr();
                    } finally {
                        executor.shutdown();
                    }
                });
                if (waitForCompletion) {
                    complete = executor.awaitTermination(failAfterInSeconds, TimeUnit.MILLISECONDS);
                    if (complete) {
                        executor.shutdownNow();
                    }
                    byte[] responseBytes = response.get();
                    exitCode = complete && failOnErrorStatusCode && ArrayUtils.isNotEmpty(responseBytes) ?
                            (int)BurpExtender.getCallbacks().getHelpers().analyzeResponse(response.get()).getStatusCode() :
                            0;
                    failed = !complete || (failOnErrorStatusCode && (exitCode == 0 || (exitCode >= 400 && exitCode < 600)));
                    if (captureOutput && (!failed || captureAfterFailure)) {
                        output = BurpExtender.getCallbacks().getHelpers().bytesToString(response.get());
                        setVariable(eventInfo, captureVariableName, output);
                    }
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
                        Pair.of("protocol", VariableString.getTextOrDefault(eventInfo, protocol, null)),
                        Pair.of("address", VariableString.getTextOrDefault(eventInfo, address, null)),
                        Pair.of("port", VariableString.getTextOrDefault(eventInfo, port, null)),
                        Pair.of("request", VariableString.getTextOrDefault(eventInfo, request, null)),
                        Pair.of("output", output),
                        Pair.of("captureVariableSource", waitForCompletion && captureOutput ? captureVariableSource : null),
                        Pair.of("variableName", waitForCompletion && captureOutput ? captureVariableName : null),
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
    public RuleOperationType<ThenSendRequest> getType() {
        return ThenType.SendRequest;
    }
}
