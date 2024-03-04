package synfron.reshaper.burp.core.messages.entities.http;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.rules.DeleteItemPlacement;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.IItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;
import synfron.reshaper.burp.core.utils.CaseInsensitiveString;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.IValue;
import synfron.reshaper.burp.core.utils.ListMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpResponseCookie extends HttpEntity implements IValue<String> {

    private String name;
    private String value;
    private String config;
    private final String headerValue;
    @Getter
    private boolean changed;

    public HttpResponseCookie(String headerValue) {
        this.headerValue = headerValue;
    }

    public HttpResponseCookie(String name, String value) {
        headerValue = String.format("%s=%s", name, value);
    }

    private void initialize() {
        if (name == null) {
            int configSeparatorIndex = headerValue.indexOf(";");
            String setter = headerValue.substring(0, configSeparatorIndex >= 0 ? configSeparatorIndex : headerValue.length());
            String[] setterParts = setter.split("=", 2);
            name = setterParts[0];
            value = CollectionUtils.elementAtOrDefault(setterParts, 1, "");
            config = configSeparatorIndex >= 0 ? headerValue.substring(configSeparatorIndex) : "";
        }
    }

    public void setCookieValue(String value) {
        initialize();
        this.value = value;
        changed = true;
    }

    public String getCookieValue() {
        initialize();
        return value;
    }

    public String getValue() {
        if (!isChanged()) {
            return headerValue;
        }
        return String.format("%s=%s%s", name, value, config);
    }

    public String getName() {
        initialize();
        return name;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
