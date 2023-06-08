package synfron.reshaper.burp.core.utils;

import burp.BurpExtender;
import burp.api.montoya.utilities.ByteUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
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

    public static byte[] asHttpMessage(String statusLine, List<String> headers, byte[] body) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteUtils byteUtils = BurpExtender.getApi().utilities().byteUtils();
        int appendIndex = 0;
        if (StringUtils.isNotEmpty(statusLine)) {
            byteArrayOutputStream.writeBytes(byteUtils.convertFromString(statusLine));
            appendIndex++;
        }
        for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
            if (headerIndex != appendIndex) {
                byteArrayOutputStream.writeBytes(byteUtils.convertFromString("\r\n"));
            }
            byteArrayOutputStream.writeBytes(byteUtils.convertFromString(headers.get(headerIndex)));
            appendIndex++;
        }
        byteArrayOutputStream.writeBytes(byteUtils.convertFromString("\r\n\r\n"));
        if (body != null && body.length > 0) {
            byteArrayOutputStream.writeBytes(body);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
