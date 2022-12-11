package synfron.reshaper.burp.core.utils;

import lombok.Getter;
import org.apache.http.client.utils.URIBuilder;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Url implements Serializable {

    @Getter
    private final String protocol;
    private final URL url;

    private final String urlValue;

    public Url(String urlValue) {
        try {
            URIBuilder uriBuilder = new URIBuilder(urlValue);
            protocol = uriBuilder.getScheme();
            uriBuilder.setScheme(Protocol.getJavaSupportProtocol(protocol));
            this.url = uriBuilder.build().toURL();
            this.urlValue = urlValue;
        } catch (URISyntaxException | MalformedURLException e) {
            throw new WrappedException(e);
        }
    }

    public Url(String protocol, String host, int port, String file)
    {
        try {
            this.protocol = protocol;
            this.url = new URL(Protocol.getJavaSupportProtocol(protocol), host, port, file);
            urlValue = new URIBuilder(url.toString()).setScheme(this.protocol).setPort(getOptionalPort()).build().toString();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new WrappedException(e);
        }
    }

    public URI toURI() {
        try {
            return new URI(urlValue);
        } catch (URISyntaxException e) {
            throw new WrappedException(e);
        }
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        return url.getPort();
    }

    public String getFile() {
        return url.getFile();
    }

    public String getAuthority() {
        return url.getAuthority();
    }

    public int getOptionalPort() {
        return Protocol.get(protocol).getDefaultPort() == url.getPort() ? -1 : url.getPort();
    }

    public Integer getDefaultPort() {
        return Protocol.get(protocol).getDefaultPort();
    }

    @Override
    public String toString() {
        return urlValue;
    }
}
