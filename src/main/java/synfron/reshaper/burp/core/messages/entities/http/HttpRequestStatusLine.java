package synfron.reshaper.burp.core.messages.entities.http;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestStatusLine extends HttpEntity {
    private boolean changed;
    private HttpRequestUri url;
    private String method;
    private String version;

    public HttpRequestStatusLine(String statusLine) {
        String[] lineParts = statusLine.split(" ", 3);
        method = CollectionUtils.elementAtOrDefault(lineParts, 0, "");
        url = new HttpRequestUri(CollectionUtils.elementAtOrDefault(lineParts, 1, ""));
        version = CollectionUtils.elementAtOrDefault(lineParts, 2, "");
    }

    public HttpRequestStatusLine(String method, String url, String version) {
        this.method = StringUtils.defaultString(method);
        this.url = new HttpRequestUri(StringUtils.defaultString(url));
        this.version = StringUtils.defaultString(version);
    }

    public HttpRequestUri getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = new HttpRequestUri(url);
        changed = true;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        changed = true;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
        changed = true;
    }

    public boolean isChanged() {
        return changed || (url != null && url.isChanged());
    }

    public String getValue() {
        return Stream.of(getMethod(), getUrl().getValue(), getVersion())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" ")
        );
    }

}
