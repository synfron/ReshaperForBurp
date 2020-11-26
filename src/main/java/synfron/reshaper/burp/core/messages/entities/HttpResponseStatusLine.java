package synfron.reshaper.burp.core.messages.entities;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionExtensions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponseStatusLine extends HttpEntity {
    private final String statusLine;
    private boolean parsed;
    @Getter
    private boolean changed;
    private String code;
    private String message;
    private String version;

    public HttpResponseStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    private void prepare() {
        if (!parsed) {
            String[] lineParts = statusLine.split(" ", 3);
            version = CollectionExtensions.elementAtOrDefault(lineParts, 0, "");
            code = CollectionExtensions.elementAtOrDefault(lineParts, 1, "");
            message = CollectionExtensions.elementAtOrDefault(lineParts, 2, "");
            parsed = true;
        }
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

    public String getCode() {
        prepare();
        return code;
    }

    public void setCode(String code) {
        prepare();
        this.code = code;
        changed = true;
    }

    public String getMessage() {
        prepare();
        return message;
    }

    public void setMessage(String message) {
        prepare();
        this.message = message;
        changed = true;
    }

    public String getValue() {
        return !isChanged() ? statusLine : Stream.of(getVersion(), getCode(), getMessage())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" ")
        );
    }

}
