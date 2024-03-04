package synfron.reshaper.burp.core.messages.entities.http;

import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.IItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;
import synfron.reshaper.burp.core.utils.IValue;

import java.util.List;

public class HttpResponseHeaders extends HttpHeaders {
    public HttpResponseHeaders(List<String> headerLines) {
        super(headerLines, "Set-Cookie");
    }

    @Override
    public String getCookie(String cookieName, GetItemPlacement itemPlacement) {
        HttpResponseCookie cookie = (HttpResponseCookie)getHeaders().getWhere(
                new CaseInsensitiveString(cookieHeaderName),
                item -> ((HttpResponseCookie) item).getName().equalsIgnoreCase(cookieName),
                itemPlacement
        );
        return cookie != null ? cookie.getCookieValue() : null;
    }

    @Override
    public void setCookie(String cookieName, String value, SetItemPlacement itemPlacement) {
        if (value != null) {
            getHeaders().computeWhereOrAdd(
                    new CaseInsensitiveString(cookieHeaderName),
                    item -> ((HttpResponseCookie) item).getName().equalsIgnoreCase(cookieName),
                    existingValue -> {
                        if (existingValue instanceof HttpResponseCookie cookie) {
                            cookie.setCookieValue(value);
                            return cookie;
                        }
                        return createCookie(cookieName, value);
                    },
                    itemPlacement
            );
        } else {
            getHeaders().removeWhere(
                    new CaseInsensitiveString(cookieHeaderName),
                    item -> ((HttpResponseCookie) item).getName().equalsIgnoreCase(cookieName),
                    IItemPlacement.toDelete(itemPlacement)
            );
        }
        changed = true;
    }

    @Override
    public IValue<String> createCookie(String value) {
        return new HttpResponseCookie(value);
    }

    @Override
    public List<String> getCookiesNames() {
        return getHeaders().getAll(new CaseInsensitiveString(cookieHeaderName)).stream()
                .map(item -> ((HttpResponseCookie)item).getName())
                .toList();
    }

    @Override
    public boolean containsCookie(String cookieName) {
        return getCookie(cookieName, GetItemPlacement.First) != null;
    }

    private IValue<String> createCookie(String name, String value) {
        return new HttpResponseCookie(name, value);
    }
}
