package synfron.reshaper.burp.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.IOException;

public class Serializer {
    private static ObjectMapper objectMapper;

    private static ObjectMapper configureMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            configureMapper(objectMapper);
            SimpleModule module = new SimpleModule();
            module.addDeserializer(VariableString.class, new VariableStringDeserializer());
            objectMapper.registerModule(module);

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

    public static <T> T deserialize(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    private static class VariableStringDeserializer extends JsonDeserializer<VariableString> {

        private final ObjectMapper objectMapper = configureMapper(new ObjectMapper());

        @Override
        public VariableString deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            return (parser.currentTokenId() == JsonTokenId.ID_STRING) ?
                    VariableString.getAsVariableString(parser.getText()) :
                    objectMapper.readValue(parser, VariableString.class);
        }
    }
}
