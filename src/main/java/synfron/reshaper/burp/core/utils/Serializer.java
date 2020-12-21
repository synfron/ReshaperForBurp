package synfron.reshaper.burp.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import synfron.reshaper.burp.core.exceptions.WrappedException;

import java.io.IOException;

public class Serializer {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            Serializer.objectMapper = objectMapper;
        }
        return objectMapper;
    }

    public static String serialize(Object value, boolean prettyPrint) {
        try  {
            return (prettyPrint ?
                    getObjectMapper().writer().withDefaultPrettyPrinter() :
                    getObjectMapper().writer()
            ).writeValueAsString(value);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static <T> T deserialize(String json, TypeReference<T> typeReference) {
        if (json == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, typeReference);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }
}
