package synfron.reshaper.burp.core.messages.entities;

import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestStatusLine extends HttpEntity {
    private final String statusLine;
    private boolean parsed;
    private boolean changed;
    private HttpRequestUri url;
    private String method;
    private String version;

    public HttpRequestStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    private void prepare() {
        if (!parsed) {
            String[] lineParts = statusLine.split(" ", 3);
            method = CollectionUtils.elementAtOrDefault(lineParts, 0, "");
            url = new HttpRequestUri(CollectionUtils.elementAtOrDefault(lineParts, 1, ""));
            version = CollectionUtils.elementAtOrDefault(lineParts, 2, "");;
            parsed = true;
        }
    }

    public HttpRequestUri getUrl() {
        prepare();
        return url;
    }

    public void setUrl(String url) {
        prepare();
        this.url = new HttpRequestUri(url);
        changed = true;
    }

    public String getVersion() {
        prepare();
        return version;
    }

    public void setVersion(String version) {
        prepare();
        this.version = version;
        changed = true;
    }

    public String getMethod() {
        prepare();
        return method;
    }

    public void setMethod(String method) {
        prepare();
        this.method = method;
        changed = true;
    }

    public boolean isChanged() {
        return changed || (url != null && url.isChanged());
    }

    public String getValue() {
        return !isChanged() ? statusLine : Stream.of(getMethod(), getUrl().getValue(), getVersion())
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.joining(" ")
        );
    }

}
