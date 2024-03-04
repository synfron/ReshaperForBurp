package synfron.reshaper.burp.core.messages.entities.http;

import synfron.reshaper.burp.core.rules.DeleteItemPlacement;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.IItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HttpHeaders extends HttpEntity {
    protected final List<String> headerLines;
    protected ListMap<CaseInsensitiveString, IValue<String>> headers;
    protected final String cookieHeaderName;
    protected boolean changed;

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
        ListMap<CaseInsensitiveString, IValue<String>> headers = getHeaders();
        if (value == null) {
            deleteHeader(name, IItemPlacement.toDelete(itemPlacement));
        } else if (name.equalsIgnoreCase(cookieHeaderName)) {
            headers.setOrAdd(new CaseInsensitiveString(name), createCookie(value), itemPlacement);
        } else {
            headers.setOrAdd(new CaseInsensitiveString(name), new Value<>(value), itemPlacement);
        }
        changed = true;
    }

    public void deleteHeader(String name, DeleteItemPlacement itemPlacement) {
        getHeaders().remove(new CaseInsensitiveString(name), itemPlacement);
        changed = true;
    }

    public abstract String getCookie(String cookieName, GetItemPlacement itemPlacement);

    public abstract void setCookie(String cookieName, String value, SetItemPlacement itemPlacement);

    public abstract IValue<String> createCookie(String value);

    public List<String> getHeaderNames() {
        return getHeaders().keys().stream().map(CaseInsensitiveString::toString).sorted().collect(Collectors.toList());
    }

    protected ListMap<CaseInsensitiveString, IValue<String>> getHeaders() {
        if (headers == null) {
            headers = new ListMap<>();
            for (String headerLine : headerLines) {
                if (headerLine.length() > 0) {
                    String[] headerParts = headerLine.split(":", 2);
                    String headerValue = CollectionUtils.elementAtOrDefault(headerParts, 1, "").stripLeading();
                    if (headerParts[0].equalsIgnoreCase(cookieHeaderName)) {
                        headers.add(new CaseInsensitiveString(headerParts[0]), createCookie(headerValue.trim()));
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
        return changed;
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

    public abstract List<String> getCookiesNames();

    public abstract boolean containsCookie(String cookieName);
}
