package synfron.reshaper.burp.core.messages.entities.http;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CollectionUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponseStatusLine extends HttpEntity {
    @Getter
    private boolean changed;
    private String code;
    private String message;
    private String version;

    public HttpResponseStatusLine(String statusLine) {
        String[] lineParts = statusLine.split(" ", 3);
        version = CollectionUtils.elementAtOrDefault(lineParts, 0, "");
        code = CollectionUtils.elementAtOrDefault(lineParts, 1, "");
        message = CollectionUtils.elementAtOrDefault(lineParts, 2, "");
    }

    public HttpResponseStatusLine(String version, String code, String message) {
        this.version = StringUtils.defaultString(version);
        this.code = StringUtils.defaultString(code);
        this.message = StringUtils.defaultString(message);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        changed = true;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        changed = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        changed = true;
    }

    public String getValue() {
        return Stream.of(getVersion(), getCode(), getMessage())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" ")
        );
    }

}
