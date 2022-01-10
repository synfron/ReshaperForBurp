package synfron.reshaper.burp.core.messages.entities;

import burp.BurpExtender;
import burp.IResponseInfo;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.ContentType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class HttpResponseMessage extends HttpEntity {

    private final byte[] response;
    private final Encoder encoder;
    private IResponseInfo responseInfo;
    private boolean changed;
    private HttpResponseStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;

    public HttpResponseMessage(byte[] response, Encoder encoder) {
        this.response = response != null ? response : new byte[0];
        this.encoder = encoder;
    }

    @Override
    public boolean isChanged() {
        return changed ||
                (statusLine != null && statusLine.isChanged()) ||
                (headers != null && headers.isChanged()) ||
                (body != null && body.isChanged());
    }

    private IResponseInfo getResponseInfo() {
        if (responseInfo == null) {
            responseInfo = BurpExtender.getCallbacks().getHelpers().analyzeResponse(response);
            if (!encoder.isUseDefault() && !getMimeType().isTextBased()) {
                encoder.setEncoding("default");
            }
        }
        return responseInfo;
    }

    public MimeType getMimeType() {
        IResponseInfo responseInfo = getResponseInfo();
        String statedMimeType = responseInfo.getStatedMimeType();
        return MimeType.get(statedMimeType.equalsIgnoreCase("svg") ?
                statedMimeType :
                StringUtils.defaultIfEmpty(responseInfo.getInferredMimeType(), statedMimeType)
        );
    }

    public HttpResponseStatusLine getStatusLine() {
        if (statusLine == null) {
            statusLine = new HttpResponseStatusLine(getResponseInfo().getHeaders().stream().findFirst().orElse(""));
        }
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = new HttpResponseStatusLine(statusLine);
        changed = true;
    }

    public HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpResponseHeaders(getResponseInfo().getHeaders().stream().skip(1).collect(toList()));
        }
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = new HttpResponseHeaders(
                Arrays.stream(headers.split("\n")).map(String::trim).collect(toList())
        );
        changed = true;
    }

    public HttpBody getBody() {
        if (this.body == null) {
            byte[] body = Arrays.copyOfRange(response, getResponseInfo().getBodyOffset(), response.length);
            this.body = new HttpBody(body, encoder);
        }
        return this.body;
    }

    public void setBody(String body) {
        this.body = new HttpBody(encoder.encode(body), encoder);
        changed = true;
    }

    public byte[] getValue() {
        return !isChanged() ?
                getAdjustedResponse(response) :
                BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                    Stream.concat(Stream.of(getStatusLine().getValue()), getHeaders().getValue().stream()).collect(Collectors.toList()),
                    getBody().getValue()
                );
    }

    private byte[] getAdjustedResponse(byte[] response) {
        IResponseInfo responseInfo = BurpExtender.getCallbacks().getHelpers().analyzeResponse(response);
        return BurpExtender.getCallbacks().getHelpers().buildHttpMessage(
                CollectionUtils.splitNewLines(responseInfo.getHeaders()),
                Arrays.copyOfRange(response, responseInfo.getBodyOffset(), response.length)
        );
    }

    public String getText() {
        return encoder.decode(getValue());
    }
}
