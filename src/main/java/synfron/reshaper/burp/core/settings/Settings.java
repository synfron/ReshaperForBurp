package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.core.type.TypeReference;
import synfron.reshaper.burp.core.utils.Serializer;

public class Settings {

    public static void store(String settingName, Object value) {
        BurpExtender.getCallbacks().saveExtensionSetting(settingName, Serializer.serialize(value));
    }

    public static <T> T get(String settingName, TypeReference<T> typeReference) {
        String json = BurpExtender.getCallbacks().loadExtensionSetting(settingName);
        if (json == null) {
            return null;
        }
        return Serializer.deserialize(json, typeReference);
    }
}
