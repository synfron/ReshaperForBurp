package synfron.reshaper.burp.core.messages.entities.http;

import burp.BurpExtender;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.messages.MimeType;
import synfron.reshaper.burp.core.utils.Log;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
        this.response = httpResponse != null ? httpResponse.toByteArray().getBytes() : new byte[0];
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
                sanityCheckHeaders();
                httpResponse = HttpResponse.httpResponse(ByteArray.byteArray(response));
            }
            if (!encoder.isUseDefault() && encoder.isAutoSet() && !getMimeType().isTextBased()) {
                encoder.setEncoding("default", true);
            }
            initialized = true;
        }
    }

    private void sanityCheckHeaders() {
        if (BurpExtender.getGeneralSettings().isEnableSanityCheckWarnings()) {
            for (int byteIndex = 0; byteIndex < response.length; byteIndex++) {
                if (response[byteIndex] == '\r') {
                    break;
                } else if (response[byteIndex] == '\n') {
                    String headersWarning = "Sanity Check - Warning: First line of a raw response with value '%s' has a line feed without a carriage return, and may result in missing headers.";
                    Log.get().withMessage(String.format(headersWarning, ByteArray.byteArray(Arrays.copyOfRange(response, 0, byteIndex)))).log();
                    break;
                }
            }
        }
    }

    public MimeType getMimeType() {
        initialize();
        return MimeType.get(ObjectUtils.defaultIfNull(httpResponse.statedMimeType(), httpResponse.inferredMimeType()));
    }

    public HttpResponseStatusLine getStatusLine() {
        if (statusLine == null) {
            initialize();
            statusLine = new HttpResponseStatusLine(httpResponse.httpVersion(), Objects.toString(httpResponse.statusCode(), ""), httpResponse.reasonPhrase());
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
            headers = new HttpResponseHeaders(httpResponse.headers().stream().map(HttpHeader::toString).collect(toList()));
        }
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = new HttpResponseHeaders(
                Arrays.stream(
                                headers.split("\n"))
                        .map(header -> StringUtils.strip(header, "\r"))
                        .filter(StringUtils::isNotEmpty).collect(Collectors.toList()
                        )
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
        return asAdjustedHttpResponse().toByteArray().getBytes();
    }

    public HttpResponse asAdjustedHttpResponse() {
        return !isChanged() ?
                getAdjustedResponse(response) :
                getAdjustedResponse(synfron.reshaper.burp.core.utils.ObjectUtils.asHttpMessage(
                        getStatusLine().getValue(),
                        getHeaders().getValue(),
                        getBody().getValue()
                ));
    }

    private HttpResponse getAdjustedResponse(byte[] response) {
        HttpResponse httpResponse = HttpResponse.httpResponse(ByteArray.byteArray(response));
        return httpResponse.withBody(httpResponse.body());
    }

    public String getText() {
        return encoder.decode(getValue());
    }
}
