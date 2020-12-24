package synfron.reshaper.burp.core.messages.entities;

import burp.BurpExtender;
import burp.IRequestInfo;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestMessage extends HttpEntity {

    private final byte[] request;
    private IRequestInfo requestInfo;
    private boolean changed;
    private HttpRequestStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;

    public HttpRequestMessage(byte[] request) {
        this.request = request != null ? request : new byte[0];
    }

    @Override
    public boolean isChanged() {
        return changed ||
                (statusLine != null && statusLine.isChanged()) ||
                (headers != null && headers.isChanged()) ||
                (body != null && body.isChanged());
    }

    private IRequestInfo getRequestInfo() {
        if (requestInfo == null) {
            requestInfo = BurpExtender.getCallbacks().getHelpers().analyzeRequest(request);
        }
        return requestInfo;
    }

    public HttpRequestStatusLine getStatusLine() {
        if (statusLine == null) {
            statusLine = new HttpRequestStatusLine(getRequestInfo().getHeaders().stream().findFirst().orElse(""));
        }
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = new HttpRequestStatusLine(statusLine);
        changed = true;
    }

    public HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpRequestHeaders(getRequestInfo().getHeaders().stream().skip(1).collect(Collectors.toList()));
        }
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = new HttpRequestHeaders(
                Arrays.stream(headers.split("\n")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList())
        );
        changed = true;
    }

    public HttpBody getBody() {
        if (this.body == null) {
            byte[] body = Arrays.copyOfRange(request, getRequestInfo().getBodyOffset(), request.length);
            this.body = new HttpBody(body);
        }
        return this.body;
    }

    public void setBody(String body) {
        this.body = new HttpBody(TextUtils.stringToBytes(body));
        changed = true;
    }

    public byte[] getValue() {
        return !isChanged() ?
                getAdjustedRequest(request) :
                BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                        Stream.concat(Stream.of(getStatusLine().getValue()), getHeaders().getValue().stream()).collect(Collectors.toList()),
                        getBody().getValue()
                );
    }

    private byte[] getAdjustedRequest(byte[] request) {
        IRequestInfo requestInfo = BurpExtender.getCallbacks().getHelpers().analyzeRequest(request);
        return BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                CollectionUtils.splitNewLines(requestInfo.getHeaders()),
                Arrays.copyOfRange(request, requestInfo.getBodyOffset(), request.length)
        );
    }

    public String getText() {
        return TextUtils.bytesToString(getValue());
    }
}
