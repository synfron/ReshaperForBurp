package synfron.reshaper.burp.core.messages.entities;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.headers.HttpHeader;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.apache.commons.lang3.ObjectUtils;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.MimeType;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class HttpResponseMessage extends HttpEntity {

    private HttpResponse httpResponse;
    private final byte[] response;
    private final Encoder encoder;
    private boolean changed;
    private HttpResponseStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;
    private boolean initialized;

    public HttpResponseMessage(HttpResponse httpResponse, Encoder encoder) {
        this.httpResponse = httpResponse;
        this.response = httpResponse != null ? httpResponse.asBytes().getBytes() : new byte[0];
        this.encoder = encoder;
    }

    public HttpResponseMessage(byte[] response, Encoder encoder) {
        this.response = response;
        this.encoder = encoder;
    }

    @Override
    public boolean isChanged() {
        return changed ||
                (statusLine != null && statusLine.isChanged()) ||
                (headers != null && headers.isChanged()) ||
                (body != null && body.isChanged());
    }

    private void initialize() {
        if (!initialized) {
            if (httpResponse == null) {
                httpResponse = HttpResponse.httpResponse(ByteArray.byteArray(response));
            }
            if (!encoder.isUseDefault() && encoder.isAutoSet() && !getMimeType().isTextBased()) {
                encoder.setEncoding("default", true);
            }
            initialized = true;
        }
    }

    public MimeType getMimeType() {
        initialize();
        return MimeType.get(ObjectUtils.defaultIfNull(httpResponse.statedMimeType(), httpResponse.inferredMimeType()));
    }

    public HttpResponseStatusLine getStatusLine() {
        if (statusLine == null) {
            initialize();
            statusLine = new HttpResponseStatusLine(httpResponse.headers().stream().map(HttpHeader::toString).findFirst().orElse(""));
        }
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = new HttpResponseStatusLine(statusLine);
        changed = true;
    }

    public HttpHeaders getHeaders() {
        if (headers == null) {
            initialize();
            headers = new HttpResponseHeaders(httpResponse.headers().stream().skip(1).map(HttpHeader::toString).collect(toList()));
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
            initialize();
            byte[] body = httpResponse.body().getBytes();
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
                asHttpResponse().asBytes().getBytes();
    }

    public HttpResponse asHttpResponse() {
        return HttpResponse.httpResponse(
                Stream.concat(Stream.of(getStatusLine().getValue()), getHeaders().getValue().stream()).collect(Collectors.toList()),
                ByteArray.byteArray(getBody().getValue())
        );
    }

    private byte[] getAdjustedResponse(byte[] response) {
        return HttpResponse.httpResponse(ByteArray.byteArray(response)).asBytes().getBytes();
    }

    public String getText() {
        return encoder.decode(getValue());
    }
}
