package synfron.reshaper.burp.core.messages.entities;

import synfron.reshaper.burp.core.utils.*;

import java.util.ArrayList;
import java.util.List;

public abstract class HttpHeaders extends HttpEntity {
    protected final List<String> headerLines;
    protected ListMap<CaseInsensitiveString, IValue<String>> headers;
    private HttpCookies cookies;
    private final String cookieHeaderName;
    private boolean changed;

    public HttpHeaders(List<String> headerLines, String cookieHeaderName) {
        this.headerLines = headerLines;
        this.cookieHeaderName = cookieHeaderName;
    }

    public String getHeader(String name, GetItemPlacement itemPlacement) {
        IValue<String> value = getHeaders().get(new CaseInsensitiveString(name), itemPlacement);
        return value != null ? value.getValue() : null;
    }

    public boolean contains(String name)
    {
        return getCount() > 0 && getHeaders().containsKey(new CaseInsensitiveString(name));
    }

    public int getCount() {
        return !isChanged() ? headerLines.size() : getHeaders().size();
    }

    public void setHeader(String name, String value, SetItemPlacement itemPlacement) {
        if (value == null) {
            deleteHeader(name, IItemPlacement.toDelete(itemPlacement));
        } else if (name.equalsIgnoreCase(cookieHeaderName)) {
            cookies = new HttpCookies(value);
            getHeaders().set(new CaseInsensitiveString(name), new Mapped<>(() -> this.cookies.getValue()), itemPlacement);
        } else {
            getHeaders().set(new CaseInsensitiveString(name), new Value<>(value), itemPlacement);
        }
        changed = true;
    }

    public void deleteHeader(String name, DeleteItemPlacement itemPlacement) {
        getHeaders().remove(new CaseInsensitiveString(name), itemPlacement);
        if (name.equalsIgnoreCase(cookieHeaderName)) {
            cookies = null;
        }
        changed = true;
    }

    public HttpCookies getCookies() {
        getHeaders();
        return cookies != null ? cookies : new HttpCookies("").withPropertyAddedListener(cookies -> {
            if (this.cookies == null) {
                this.cookies = cookies;
                headers.setLast(new CaseInsensitiveString(cookieHeaderName), new Mapped<>(() -> this.cookies.getValue()));
            }
        });
    }

    private ListMap<CaseInsensitiveString, IValue<String>> getHeaders() {
        if (headers == null) {
            headers = new ListMap<>();
            for (String headerLine : headerLines) {
                if (headerLine.length() > 0) {
                    String[] headerParts = headerLine.split(":", 2);
                    String headerValue = CollectionUtils.elementAtOrDefault(headerParts, 1, "").stripLeading();
                    if (headerParts[0].equalsIgnoreCase(cookieHeaderName)) {
                        cookies = new HttpCookies(headerParts[1].trim());
                        headers.add(new CaseInsensitiveString(headerParts[0]), new Mapped<>(() -> this.cookies.getValue()));
                    } else {
                        headers.add(new CaseInsensitiveString(headerParts[0]), new Value<>(headerValue.trim()));
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
        for (var headerEntry : getHeaders().entries()) {
            headerLines.add(String.format("%s: %s", headerEntry.getKey(), headerEntry.getValue()));
        }
        return headerLines;
    }

    public String getText() {
        return String.join("\r\n", getValue());
    }
}
