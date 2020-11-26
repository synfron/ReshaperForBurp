package synfron.reshaper.burp.core.utils;

public class CollectionExtensions {
    public static <T> T elementAtOrDefault(T[] array, int index) {
        return elementAtOrDefault(array, index, null);
    }

    public static <T> T elementAtOrDefault(T[] array, int index, T defaultValue) {
        return array != null && array.length > index ? array[index] : defaultValue;
    }
}
