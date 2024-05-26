package synfron.reshaper.burp.core.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class ContentType {
    public static final ContentType None = new ContentType("None", 1);
    public static final ContentType UrlEncoded = new ContentType("URL Encoded", 2);
    public static final ContentType MultiPart = new ContentType("Multi-Part", 4);
    public static final ContentType Xml = new ContentType("XML", 8);
    public static final ContentType Json = new ContentType("JSON", 16);
    public static final ContentType Amf = new ContentType("AMF", 32);
    public static final ContentType Unknown = new ContentType("Unknown", 64);

    @Getter
    private static final List<ContentType> values = List.of(
            None,
            UrlEncoded,
            MultiPart,
            Xml,
            Json,
            Amf,
            Unknown
    );

    @Getter
    private final String[] names;
    private final int flags;

    private ContentType() {
        this(None.names[0], None.flags);
    }

    private ContentType(String name, int flags) {
        this.names = new String[] { name };
        this.flags = flags;
    }

    private ContentType(int flags) {
        List<ContentType> contentTypes = getValues().stream()
                .filter(contentType -> contentType.hasFlags(flags))
                .toList();
        this.names = contentTypes.stream()
                .map(ContentType::getName).toArray(String[]::new);
        this.flags = flags;
    }

    public String getName() {
        return String.join(", ", names);
    }

    public static ContentType get(burp.api.montoya.http.message.ContentType contentType) {
        return switch (contentType) {
            case AMF -> Amf;
            case XML -> Xml;
            case JSON -> Json;
            case NONE -> None;
            case MULTIPART -> MultiPart;
            case URL_ENCODED -> UrlEncoded;
            case UNKNOWN -> Unknown;
        };
    }

    public boolean isTextBased() {
        return flags != 0 && (flags & ~(Json.flags | UrlEncoded.flags | Xml.flags)) == 0;
    }

    public boolean hasFlags(ContentType contentType) {
        return hasFlags(contentType.flags);
    }

    private boolean hasFlags(int flags) {
        return (this.flags & flags) == flags;
    }

    public ContentType or(ContentType contentType) {
        int newFlag = flags | contentType.flags;
        return new ContentType(newFlag);
    }

    public ContentType minus(ContentType contentType) {
        int newFlag = flags - contentType.flags;
        return new ContentType(newFlag);
    }

    @Override
    public String toString() {
       return getName();
    }
}
