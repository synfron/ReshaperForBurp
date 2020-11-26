package synfron.reshaper.burp.core.settings;

import burp.BurpExtender;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.io.IOException;

public class Settings {
    public static void store(String settingName, Object value) {
        try  {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
            objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            BurpExtender.getCallbacks().saveExtensionSetting(settingName, objectMapper.writeValueAsString(value));
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static <T> T get(String settingName, TypeReference<T> typeReference) {
        String json = BurpExtender.getCallbacks().loadExtensionSetting(settingName);
        if (json == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }
}
