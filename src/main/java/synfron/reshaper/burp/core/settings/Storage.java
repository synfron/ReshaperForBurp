package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.utils.Serializer;

public class Storage {

    public static void store(String storageKey, Object value) {
        BurpExtender.getApi().persistence().preferences().setString(storageKey, Serializer.serialize(value, false));
    }

    public static <T> T get(String storageKey, TypeReference<T> typeReference) {
        String json = BurpExtender.getApi().persistence().preferences().getString(storageKey);
        if (json == null) {
            return null;
        }
        return Serializer.deserialize(json, typeReference);
    }
}
