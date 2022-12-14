package synfron.reshaper.burp.core.utils;

import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.lang.reflect.InvocationTargetException;
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
}
