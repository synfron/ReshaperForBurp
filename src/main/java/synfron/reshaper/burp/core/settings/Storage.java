package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.utils.Serializer;

public class Storage {

    public static void store(String storageKey, Object value) {
        BurpExtender.getCallbacks().saveExtensionSetting(storageKey, Serializer.serialize(value, false));
    }

    public static <T> T get(String storageKey, TypeReference<T> typeReference) {
        String json = BurpExtender.getCallbacks().loadExtensionSetting(storageKey);
        if (json == null) {
            return null;
        }
        return Serializer.deserialize(json, typeReference);
    }
}
