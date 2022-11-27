package synfron.reshaper.burp.core.messages.entities;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.headers.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.messages.ContentType;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.utils.SetItemPlacement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestMessage extends HttpEntity {

    private HttpRequest httpRequest;
    private final byte[] request;
    private final Encoder encoder;
    private boolean changed;
    private HttpRequestStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;
    private boolean initialized;

    public HttpRequestMessage(HttpRequest httpRequest, Encoder encoder) {
        this.httpRequest = httpRequest;
        this.request = httpRequest != null ? httpRequest.asBytes().getBytes() : new byte[0];
        this.encoder = encoder;
    }

    public HttpRequestMessage(byte[] request, Encoder encoder) {
        this.request = request;
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
            if (httpRequest == null) {
                httpRequest = HttpRequest.httpRequest(ByteArray.byteArray(request));
            }
            if (!encoder.isUseDefault() && encoder.isAutoSet() && !getContentType().isTextBased()) {
                encoder.setEncoding("default", true);
            }
            initialized = true;
        }
    }

    public ContentType getContentType() {
        initialize();
        return ContentType.get(httpRequest.contentType());
    }

    public HttpRequestStatusLine getStatusLine() {
        if (statusLine == null) {
            initialize();
            statusLine = new HttpRequestStatusLine(httpRequest.headers().stream().map(HttpHeader::toString).findFirst().orElse(""));
        }
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = new HttpRequestStatusLine(statusLine);
        changed = true;
    }

    public HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpRequestHeaders(httpRequest.headers().stream().map(HttpHeader::toString).skip(1).collect(Collectors.toList()));
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
            byte[] body = httpRequest.body().getBytes();
            this.body = new HttpBody(body, encoder);
        }
        return this.body;
    }

    public void setBody(String body) {
        this.body = new HttpBody(encoder.encode(body), encoder);
        changed = true;
    }

    public void setUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            setUrl(url);
        } catch (MalformedURLException e) {
            throw new WrappedException(e);
        }
    }

    public void setUrl(URL url) {
        getStatusLine().setUrl(url.getFile().startsWith("/") ? url.getFile() : "/" + url.getFile());
        getHeaders().setHeader("Host", url.getAuthority(), SetItemPlacement.Only);
    }

    public byte[] getValue() {
        return !isChanged() ?
                getAdjustedRequest(request) :
                asHttpRequest().asBytes().getBytes();
    }

    public HttpRequest asHttpRequest() {
        return HttpRequest.httpRequest(
                null,
                Stream.concat(Stream.of(getStatusLine().getValue()), getHeaders().getValue().stream()).collect(Collectors.toList()),
                ByteArray.byteArray(getBody().getValue())
        );
    }

    private byte[] getAdjustedRequest(byte[] request) {
       return HttpRequest.httpRequest(ByteArray.byteArray(request)).asBytes().getBytes();
    }

    public String getText() {
        return encoder.decode(getValue());
    }
}
