package synfron.reshaper.burp.core.messages.entities;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpCookies extends HttpEntity {
    private ListMap<CaseInsensitiveString, String> cookies;
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

    public void setCookie(String name, String value, SetItemPlacement itemPlacement) {
        if (value != null) {
            getCookies().set(new CaseInsensitiveString(name), value, itemPlacement);
            changed = true;
            propertyAdded();
        } else {
            deleteCookie(name, IItemPlacement.toDelete(itemPlacement));
        }
    }

    public void deleteCookie(String name, DeleteItemPlacement itemPlacement) {
        getCookies().remove(new CaseInsensitiveString(name), itemPlacement);
        changed = true;
    }

    public String getCookie(String name, GetItemPlacement itemPlacement) {
        return getCookies().get(new CaseInsensitiveString(name), itemPlacement);
    }

    public List<String> getCookiesNames() {
        return getCookies().keys().stream().map(CaseInsensitiveString::toString).sorted().collect(Collectors.toList());
    }

    public boolean contains(String name)
    {
        return getCount() > 0 && getCookies().containsKey(new CaseInsensitiveString(name));
    }

    private ListMap<CaseInsensitiveString, String> getCookies() {
        if (cookies == null) {
            cookies = new ListMap<>();
            if (StringUtils.isNotEmpty(headerValue)) {
                String[] cookieEntries = headerValue.split(";");
                for (String cookieEntry : cookieEntries) {
                    String[] cookieEntryParts = cookieEntry.split("=", 2);
                    cookies.add(
                            new CaseInsensitiveString(cookieEntryParts[0].trim()),
                            CollectionUtils.elementAtOrDefault(cookieEntryParts, 1, "").stripLeading()
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
        for (var cookieEntry : getCookies().entries()) {
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
