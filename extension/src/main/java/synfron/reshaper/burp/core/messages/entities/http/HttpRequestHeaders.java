package synfron.reshaper.burp.core.messages.entities.http;

import java.util.List;

public class HttpRequestHeaders extends HttpHeaders {
    public HttpRequestHeaders(List<String> headerLines) {
        super(headerLines, "Cookie");
    }
}
