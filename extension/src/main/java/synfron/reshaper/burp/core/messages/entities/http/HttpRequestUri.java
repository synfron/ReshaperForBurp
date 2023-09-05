package synfron.reshaper.burp.core.messages.entities.http;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpRequestUri extends HttpEntity {

    private final String uri;
    private URIBuilder uriBuilder;
    private HttpRequestQueryParams queryParams;
    @Getter
    private boolean changed;

    public HttpRequestUri(String uri) {
        this.uri = uri;
    }

    public void prepare() {
        if (uriBuilder == null) {
            try {
                uriBuilder = new URIBuilder(uri);
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
        uriBuilder.setPath(StringUtils.defaultIfBlank(path, "/"));
        changed = true;
    }

    public HttpRequestQueryParams getQueryParams() {
        prepare();
        if (queryParams == null) {
            queryParams = new HttpRequestQueryParams(uriBuilder.getQueryParams());
        }
        return queryParams;
    }

    public String getQueryParametersText() {
        prepare();
        return URLEncodedUtils.format(uriBuilder.getQueryParams(), ObjectUtils.defaultIfNull(uriBuilder.getCharset(), StandardCharsets.UTF_8));

    }

    public void setQueryParametersText(String parameters) {
        prepare();
        uriBuilder.setParameters(URLEncodedUtils.parse(parameters, ObjectUtils.defaultIfNull(uriBuilder.getCharset(), StandardCharsets.UTF_8)));
        queryParams = null;
        changed = true;
    }

    public String getValue() {
        boolean isQueryParamsChanged = queryParams != null && queryParams.isChanged();
        if (isChanged() || isQueryParamsChanged) {
            if (isQueryParamsChanged) {
                uriBuilder.setParameters(queryParams.getValue());
            }
            return uriBuilder.toString();
        } else {
            return uri;
        }
    }
}
