package synfron.reshaper.burp.core.utils;

import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Stream;

public class ObjectUtils {
    public static Object construct(Class<?> clazz, Object... args) {
        try {
            return clazz.getDeclaredConstructor(
                    Stream.of(args).map(Object::getClass).toArray(Class[]::new)
            ).newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new WrappedException(e);
        }
    }

    public static URL getUrl(String protocol, String host, int port, String path) throws MalformedURLException {
        return new URL(
                protocol,
                host,
                (Objects.equals(protocol, "http") && port == 80) || (Objects.equals(protocol, "https") && port == 443) ? -1 : port,
                path
        );
    }
}
