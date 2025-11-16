package synfron.reshaper.burp.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.Getter;
import synfron.reshaper.burp.core.exceptions.WrappedException;
import synfron.reshaper.burp.core.rules.thens.Then;
import synfron.reshaper.burp.core.rules.thens.entities.generate.IGenerator;
import synfron.reshaper.burp.core.rules.thens.entities.transform.ITransformer;
import synfron.reshaper.burp.core.rules.whens.When;
import synfron.reshaper.burp.core.vars.VariableString;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serializer {
    @Getter
    private static ObjectMapper objectMapper;
    private static final JsonFactory jsonFactory = new JsonFactory();
    private static List<Class<?>> subTypedClasses = List.of(
      When.class,
      Then.class,
      IGenerator.class,
      ITransformer.class
    );

    static {
        List<JsonDeserializer<?>> deserializers = new ArrayList<>();
        deserializers.add(new VariableStringDeserializer());
        subTypedClasses.forEach(subTypedClass -> deserializers.add(new SubTypeDeserializer(subTypedClass)));
        Serializer.objectMapper = configureMapper(List.of(new VariableStringSerializer()), deserializers);
    }

    private static ObjectMapper configureMapper() {
        return configureMapper(List.of(), List.of());
    }

    private static ObjectMapper configureMapper(List<JsonSerializer<?>> serializers, List<JsonDeserializer<?>> deserializers) {
        JsonMapper.Builder builder = JsonMapper.builder(YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).build());
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        builder.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        builder.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        builder.visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        builder.visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        builder.visibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        builder.visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        builder.visibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);
        if (!serializers.isEmpty() || !deserializers.isEmpty()) {
            SimpleModule module = new SimpleModule();
            serializers.forEach(module::addSerializer);
            deserializers.forEach(deserializer -> addDeserializer(module, deserializer.handledType(), deserializer));
            builder.addModule(module);
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T> void addDeserializer(SimpleModule module, Class<T> type, JsonDeserializer<?> deserializer) {
        module.addDeserializer(type, (JsonDeserializer<? extends T>)deserializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> T copy(T source) {
        return deserialize(serialize(source, false), (Class<T>)source.getClass());
    }

    public static String serialize(Object value, boolean prettyPrint) {
        try  {
            StringWriter stringWriter = new StringWriter();
            (prettyPrint ?
                    objectMapper.writer().withDefaultPrettyPrinter() :
                    objectMapper.writer()
            ).writeValue(jsonFactory.createGenerator(stringWriter).configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true), value);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static String serializeYaml(Object value, boolean prettyPrint) {
        try  {
            return (prettyPrint ?
                    objectMapper.writer().withDefaultPrettyPrinter() :
                    objectMapper.writer()
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
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public static <C> Map<String, Class<? extends C>> getJsonSubTypes(Class<C> parentType) {
        Map<String, Class<? extends C>> result = new HashMap<>();

        if (parentType.isAnnotationPresent(JsonSubTypes.class)) {
            JsonSubTypes annotation = parentType.getAnnotation(JsonSubTypes.class);
            for (JsonSubTypes.Type type : annotation.value()) {
                result.put(type.value().getSimpleName(), type.value().asSubclass(parentType));
            }
        }

        return result;
    }

    private static class SubTypeDeserializer<T> extends JsonDeserializer<T> {

    private final Map<String, Class<? extends T>> subTypes;
        private final Class<T> type;

        public SubTypeDeserializer(Class<T> type) {
            this.type = type;
            this.subTypes = getJsonSubTypes(type);
        }

        @Override
        public Class<T> handledType() {
            return type;
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            String className = node.get("@class").asText();
            className = className.substring(className.lastIndexOf('.') + 1);
            return parser.getCodec().treeToValue(node, subTypes.get(className));
        }
    }

    private static class VariableStringDeserializer extends JsonDeserializer<VariableString> {
        @Override
        public Class<VariableString> handledType() {
            return VariableString.class;
        }

        @Override
        public VariableString deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ObjectMapper objectMapper = configureMapper();
            return switch (parser.currentTokenId()) {
                case JsonTokenId.ID_STRING,
                        JsonTokenId.ID_FALSE,
                        JsonTokenId.ID_TRUE,
                        JsonTokenId.ID_NUMBER_FLOAT,
                        JsonTokenId.ID_NUMBER_INT -> VariableString.getAsVariableString(parser.getText());
                default -> objectMapper.readValue(parser, VariableString.class);
            };
        }
    }

    private static class VariableStringSerializer extends JsonSerializer<VariableString> {
        @Override
        public Class<VariableString> handledType() {
            return VariableString.class;
        }

        @Override
        public void serialize(VariableString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }
}
