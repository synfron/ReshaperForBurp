package synfron.reshaper.burp.core.messages.entities;

import java.util.List;

public class HttpResponseHeaders extends HttpHeaders {
    public HttpResponseHeaders(List<String> headerLines) {
        super(headerLines, "Set-Cookie");
    }
}
