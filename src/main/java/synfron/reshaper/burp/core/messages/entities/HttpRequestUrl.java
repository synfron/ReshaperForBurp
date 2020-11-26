package synfron.reshaper.burp.core.messages.entities;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpRequestUrl extends HttpEntity {

    private final String url;
    private URIBuilder uriBuilder;
    @Getter
    private boolean changed;

    public HttpRequestUrl(String url) {
        this.url = url;
    }

    public void prepare() {
        if (uriBuilder == null) {
            try {
                uriBuilder = new URIBuilder(url);
            } catch (URISyntaxException e) {
                throw new WrappedException(e);
            }
        }
    }

    public String getPath() {
        prepare();
        return uriBuilder.getPath();
    }

    public void setPath(String path) {
        prepare();
        uriBuilder.setPath(path);
        changed = true;
    }

    public String getQueryParameter(String name) {
        prepare();
        return uriBuilder.getQueryParams().stream()
                .filter(entry -> entry.getName().equals(name))
                .map(NameValuePair::getValue)
                .findFirst().orElse(null);
    }

    public void setQueryParameter(String name, String value) {
        if (value != null) {
            prepare();
            uriBuilder.addParameter(name, value);
            changed = true;
        } else {
            deleteParameter(name);
        }
    }

    public void deleteParameter(String name) {
        uriBuilder.setParameters(
            uriBuilder.getQueryParams().stream().filter(param -> !param.getName().equals(name)).toArray(NameValuePair[]::new)
        );
    }

    public String getQueryParameters() {
        prepare();
        return URLEncodedUtils.format(uriBuilder.getQueryParams(), ObjectUtils.defaultIfNull(uriBuilder.getCharset(), StandardCharsets.UTF_8));

    }

    public void setQueryParameters(String parameters) {
        prepare();
        uriBuilder.setParameters(URLEncodedUtils.parse(parameters, ObjectUtils.defaultIfNull(uriBuilder.getCharset(), StandardCharsets.UTF_8)));
        changed = true;
    }

    public String getValue() {
        return !isChanged() ? url : uriBuilder.toString();
    }
}
