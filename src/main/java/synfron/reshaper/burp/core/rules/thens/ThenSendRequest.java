package synfron.reshaper.burp.core.rules.thens;

import burp.BurpExtender;
import burp.api.montoya.http.HttpMode;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.messages.HttpEventInfo;
import synfron.reshaper.burp.core.messages.entities.http.HttpResponseMessage;
import synfron.reshaper.burp.core.rules.IHttpRuleOperation;
import synfron.reshaper.burp.core.rules.IWebSocketRuleOperation;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.*;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ThenSendRequest extends Then<ThenSendRequest> implements IHttpRuleOperation, IWebSocketRuleOperation {
    @Getter @Setter
    private VariableString request;
    @Getter @Setter
    private VariableString url;
    @Getter @Setter
    private VariableString protocol;
    @Getter @Setter
    private VariableString address;
    @Getter @Setter
    private VariableString port;
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
        int statusCode = 0;
        String captureVariableName = null;
        try {
            int failAfterInMilliseconds = waitForCompletion ? getFailAfter(eventInfo) : 0;
            captureVariableName = getVariableName(eventInfo);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AtomicReference<HttpResponse> response = new AtomicReference<>();
            executor.submit(() -> {
                try {
                    HttpEventInfo newEventInfo = new HttpEventInfo(eventInfo);
                    boolean useHttps = !StringUtils.equalsIgnoreCase(newEventInfo.getHttpProtocol(), "http");
                    if (!VariableString.isEmpty(request)) {
                        newEventInfo.setHttpRequestMessage(eventInfo.getEncoder().encode(this.request.getText(eventInfo)));
                    }
                    if (!VariableString.isEmpty(url)) {
                        newEventInfo.setUrl(url.getText(eventInfo));
                        useHttps = !StringUtils.equalsIgnoreCase(newEventInfo.getHttpProtocol(), "http");
                    }
                    if (!VariableString.isEmpty(protocol)) {
                        useHttps = !StringUtils.equalsIgnoreCase(protocol.getText(eventInfo), "http");
                    }
                    if (!VariableString.isEmpty(address)) {
                        newEventInfo.setDestinationAddress(address.getText(eventInfo));
                    }
                    if (!VariableString.isEmpty(port)) {
                        newEventInfo.setDestinationPort(VariableString.getIntOrDefault(
                                eventInfo,
                                this.port,
                                (newEventInfo.getDestinationPort() == null || newEventInfo.getDestinationPort() == 0) ?
                                        (useHttps ? 443 : 80) :
                                        newEventInfo.getDestinationPort()
                        ));
                    }

                    HttpResponse httpResponse = BurpExtender.getApi().http().sendRequest(
                            newEventInfo.asHttpRequest(),
                            HttpMode.AUTO
                    ).response();
                    response.set(httpResponse);
                } catch (Exception e) {
                    Log.get().withMessage("Failure sending request").withException(e).logErr();
                } finally {
                    executor.shutdown();
                }
            });
            if (waitForCompletion) {
                complete = executor.awaitTermination(failAfterInMilliseconds, TimeUnit.MILLISECONDS);
                if (complete) {
                    executor.shutdownNow();
                }
                HttpResponse httpResponse = response.get();
                statusCode = complete && failOnErrorStatusCode && httpResponse != null ?
                        httpResponse.statusCode() :
                        0;
                failed = !complete || (failOnErrorStatusCode && (statusCode == 0 || (statusCode >= 400 && statusCode < 600)));
                if (captureOutput && (!failed || captureAfterFailure)) {
                    output = response.get() != null ? new HttpResponseMessage(response.get().toByteArray().getBytes(), eventInfo.getEncoder()).getText() : "";
                    setVariable(captureVariableSource, eventInfo, captureVariableName, output);
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
                        Pair.of("url", VariableString.getTextOrDefault(eventInfo, url, null)),
                        Pair.of("request", VariableString.getTextOrDefault(eventInfo, request, null)),
                        Pair.of("protocol", VariableString.getTextOrDefault(eventInfo, protocol, null)),
                        Pair.of("address", VariableString.getTextOrDefault(eventInfo, address, null)),
                        Pair.of("port", VariableString.getTextOrDefault(eventInfo, port, null)),
                        Pair.of("output", output),
                        Pair.of("captureVariableSource", waitForCompletion && captureOutput ? captureVariableSource : null),
                        Pair.of("captureVariableName", waitForCompletion && captureOutput ? captureVariableName : null),
                        Pair.of("exceededWait", waitForCompletion ? !complete : null),
                        Pair.of("failed", waitForCompletion ? failed : null),
                        Pair.of("exitCode", waitForCompletion ? statusCode : null)
                ));
        }
        return failed && breakAfterFailure ? RuleResponse.BreakRules : RuleResponse.Continue;
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
