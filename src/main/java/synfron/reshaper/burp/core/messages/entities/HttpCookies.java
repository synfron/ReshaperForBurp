package synfron.reshaper.burp.core.messages.entities;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;
import synfron.reshaper.burp.core.utils.CollectionExtensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class HttpCookies extends HttpEntity {
    private LinkedHashMap<CaseInsensitiveString, String> cookies;
    private final String headerValue;
    @Getter
    private boolean changed;
    @Getter @Setter
    private Consumer<HttpCookies> propertyAddedListener;

    public HttpCookies(String headerValue) {
        this.headerValue = headerValue;
    }

    public int getCount() {
        return headerValue != null && headerValue.length() == 0 ? 0 : getCookies().size();
    }

    public void setCookie(String name, String value) {
        if (value != null) {
            getCookies().put(new CaseInsensitiveString(name), value);
            changed = true;
            propertyAdded();
        } else {
            deleteCookie(name);
        }
    }

    public void deleteCookie(String name) {
        getCookies().remove(new CaseInsensitiveString(name));
        changed = true;
    }

    public String getCookie(String name) {
        return getCookies().get(new CaseInsensitiveString(name));
    }

    public boolean contains(String name)
    {
        return getCount() > 0 && getCookies().containsKey(new CaseInsensitiveString(name));
    }

    private LinkedHashMap<CaseInsensitiveString, String> getCookies() {
        if (cookies == null) {
            cookies = new LinkedHashMap<>();
            if (StringUtils.isNotEmpty(headerValue)) {
                String[] cookieEntries = headerValue.split(";");
                for (String cookieEntry : cookieEntries) {
                    String[] cookieEntryParts = cookieEntry.split("=", 2);
                    cookies.put(
                            new CaseInsensitiveString(cookieEntryParts[0]),
                            CollectionExtensions.elementAtOrDefault(cookieEntryParts, 1, "").stripLeading()
                    );
                }
            }
        }
        return cookies;
    }

    public String getValue() {
        if (!isChanged()) {
            return headerValue;
        }

        List<String> cookieEntries = new ArrayList<>();
        for (var cookieEntry : getCookies().entrySet()) {
            cookieEntries.add(cookieEntry.getValue().equals("") ?
                    cookieEntry.getKey().getValue() :
                    String.format("%s=%s", cookieEntry.getKey(), cookieEntry.getValue())
            );
        }
        return String.join("; ", cookieEntries);
    }

    private void propertyAdded() {
        if (propertyAddedListener != null) {
            propertyAddedListener.accept(this);
        }
    }

    public HttpCookies withPropertyAddedListener(Consumer<HttpCookies> consumer) {
        propertyAddedListener = consumer;
        return this;
    }
}
