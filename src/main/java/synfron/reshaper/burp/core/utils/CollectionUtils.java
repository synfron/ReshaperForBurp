package synfron.reshaper.burp.core.utils;

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
}
