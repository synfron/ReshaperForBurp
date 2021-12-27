package synfron.reshaper.burp.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {
    public static <T> T elementAtOrDefault(T[] array, int index) {
        return elementAtOrDefault(array, index, null);
    }

    public static <T> T elementAtOrDefault(T[] array, int index, T defaultValue) {
        return array != null && array.length > index ? array[index] : defaultValue;
    }

    public static byte[] defaultIfEmpty(byte[] array, byte[] defaultArray) {
        return (array != null && array.length > 0) ? array : defaultArray;
    }

    public static <T> boolean hasAny(List<T> list) {
        return list != null && !list.isEmpty();
    }

    public static Stream<String> splitNewLines(Stream<String> values) {
        return values.flatMap(value -> Arrays.stream(value.split("\n")).filter(StringUtils::isNotEmpty));
    }

    public static List<String> splitNewLines(List<String> values) {
        return splitNewLines(values.stream()).collect(Collectors.toList());
    }
}
