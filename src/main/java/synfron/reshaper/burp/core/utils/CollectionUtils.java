package synfron.reshaper.burp.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
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

    public static <T> T elementAtOrDefault(List<T> list, int index) {
        return elementAtOrDefault(list, index, null);
    }

    public static <T> T elementAtOrDefault(List<T> list, int index, T defaultValue) {
        return list != null && list.size() > index ? list.get(index) : defaultValue;
    }

    public static byte[] defaultIfEmpty(byte[] array, byte[] defaultArray) {
        return (array != null && array.length > 0) ? array : defaultArray;
    }

    public static <T> List<T> defaultIfNull(List<T> list) {
        return list != null ? list : List.of();
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

    @SuppressWarnings("unchecked")
    public static <T> T[] move(T[] array, int currentIndex, int newIndex) {
        if (currentIndex - newIndex == 1 || currentIndex - newIndex == -1) {
            T[] newArray = ArrayUtils.clone(array);
            ArrayUtils.swap(newArray, currentIndex, newIndex);
            return newArray;
        }

        if (currentIndex < 0 || currentIndex >= array.length) {
            throw new IndexOutOfBoundsException(String.format("Index: %s, Size: %s", currentIndex, array.length));
        }
        if (newIndex < 0 || newIndex >= array.length) {
            throw new IndexOutOfBoundsException(String.format("Index: %s, Size: %s", newIndex, array.length));
        }

        final T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);

        if (currentIndex < newIndex) {
            System.arraycopy(array, 0, result, 0, currentIndex);
            System.arraycopy(array, currentIndex + 1, result, currentIndex, newIndex - currentIndex);
            if (newIndex + 1 < array.length) {
                System.arraycopy(array, newIndex + 1, result, newIndex + 1, array.length - (newIndex + 1));
            }
        } else {
            System.arraycopy(array, 0, result, 0, newIndex);
            System.arraycopy(array, newIndex, result, newIndex + 1, currentIndex - newIndex);
            if (currentIndex + 1 < array.length) {
                System.arraycopy(array, currentIndex + 1, result, currentIndex + 1, array.length - (currentIndex + 1));
            }
        }
        result[newIndex] = array[currentIndex];
        return result;
    }
}
