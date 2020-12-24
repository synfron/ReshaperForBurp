package synfron.reshaper.burp.core.messages.entities;

import synfron.reshaper.burp.core.utils.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class HttpHeaders extends HttpEntity {
    protected final List<String> headerLines;
    protected LinkedHashMap<CaseInsensitiveString, IValue<String>> headers;
    private HttpCookies cookies;
    private final String cookieHeaderName;
    private boolean changed;

    public HttpHeaders(List<String> headerLines, String cookieHeaderName) {
        this.headerLines = headerLines;
        this.cookieHeaderName = cookieHeaderName;
    }

    public String getHeader(String name) {
        IValue<String> value = getHeaders().get(new CaseInsensitiveString(name));
        return value != null ? value.getValue() : null;
    }

    public boolean contains(String name)
    {
        return getCount() > 0 && getHeaders().containsKey(new CaseInsensitiveString(name));
    }

    public int getCount() {
        return !isChanged() ? headerLines.size() : getHeaders().size();
    }

    public void setHeader(String name, String value) {
        if (value == null) {
            deleteHeader(name);
        } else if (name.toLowerCase().equals(cookieHeaderName.toLowerCase())) {
            cookies = new HttpCookies(value);
            getHeaders().put(new CaseInsensitiveString(name), new Mapped<>(() -> this.cookies.getValue()));
        } else {
            getHeaders().put(new CaseInsensitiveString(name), new Value<>(value));
        }
        changed = true;
    }

    public void deleteHeader(String name) {
        getHeaders().remove(new CaseInsensitiveString(name));
        if (name.toLowerCase().equals(cookieHeaderName.toLowerCase())) {
            cookies = null;
        }
        changed = true;
    }

    public HttpCookies getCookies() {
        getHeaders();
        return cookies != null ? cookies : new HttpCookies("").withPropertyAddedListener(cookies -> {
            if (this.cookies == null) {
                this.cookies = cookies;
                headers.put(new CaseInsensitiveString(cookieHeaderName), new Mapped<>(() -> this.cookies.getValue()));
            }
        });
    }

    private LinkedHashMap<CaseInsensitiveString, IValue<String>> getHeaders() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
            for (String headerLine : headerLines) {
                if (headerLine.length() > 0) {
                    String[] headerParts = headerLine.split(":", 2);
                    String headerValue = CollectionUtils.elementAtOrDefault(headerParts, 1, "").stripLeading();
                    if (headerParts[0].toLowerCase().equals(cookieHeaderName.toLowerCase())) {
                        cookies = new HttpCookies(headerParts[1].trim());
                        headers.put(new CaseInsensitiveString(headerParts[0]), new Mapped<>(() -> this.cookies.getValue()));
                    } else {
                        headers.put(new CaseInsensitiveString(headerParts[0]), new Value<>(headerValue.trim()));
                    }
                }
            }
        }
        return headers;
    }

    @Override
    public boolean isChanged() {
        return changed || (cookies != null && cookies.isChanged());
    }

    public List<String> getValue() {
        if (!isChanged()) {
            return CollectionUtils.splitNewLines(this.headerLines);
        }

        List<String> headerLines = new ArrayList<>();
        for (var headerEntry : getHeaders().entrySet()) {
            headerLines.add(String.format("%s: %s", headerEntry.getKey(), headerEntry.getValue()));
        }
        return headerLines;
    }

    public String getText() {
        return String.join("\r\n", getValue());
    }
}
