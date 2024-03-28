package synfron.reshaper.burp.core.messages.entities.http;

import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;
import synfron.reshaper.burp.core.utils.IValue;

import java.util.List;

public class HttpRequestHeaders extends HttpHeaders {
    public HttpRequestHeaders(List<String> headerLines) {
        super(headerLines, "Cookie");
    }

    @Override
    public String getCookie(String cookieName, GetItemPlacement itemPlacement) {
        HttpRequestCookies cookies = (HttpRequestCookies)getHeaders().get(new CaseInsensitiveString(cookieHeaderName), GetItemPlacement.Last);
        if (cookies != null) {
            return cookies.getCookie(cookieName, itemPlacement);
        }
        return null;
    }

    @Override
    public void setCookie(String cookieName, String value, SetItemPlacement itemPlacement) {
        HttpRequestCookies cookies = (HttpRequestCookies)getHeaders().get(new CaseInsensitiveString(cookieHeaderName), GetItemPlacement.Last);
        if (cookies != null) {
            cookies.setCookie(cookieName, value, itemPlacement);
            if (value == null) {
                if (cookies.getCount() == 0) {
                    getHeaders().removeLast(new CaseInsensitiveString(cookieHeaderName));
                }
            }
        } else if (value != null) {
            getHeaders().add(new CaseInsensitiveString(cookieHeaderName), createCookie(cookieName, value));
        }
        changed = true;
    }

    @Override
    public IValue<String> createCookie(String value) {
        return new HttpRequestCookies(value);
    }

    @Override
    public List<String> getCookiesNames() {
        HttpRequestCookies cookies = (HttpRequestCookies)getHeaders().get(new CaseInsensitiveString(cookieHeaderName), GetItemPlacement.Last);
        if (cookies != null) {
            return cookies.getCookiesNames();
        }
        return List.of();
    }

    @Override
    public boolean containsCookie(String cookieName) {
        HttpRequestCookies cookies = (HttpRequestCookies)getHeaders().get(new CaseInsensitiveString(cookieHeaderName), GetItemPlacement.Last);
        return cookies != null && cookies.contains(cookieName);
    }

    private IValue<String> createCookie(String name, String value) {
        return new HttpRequestCookies(String.format("%s=%s", name, value));
    }
}
