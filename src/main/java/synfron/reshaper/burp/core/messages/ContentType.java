package synfron.reshaper.burp.core.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class ContentType {
    public static final ContentType None = new ContentType("None", 0, 1);
    public static final ContentType UrlEncoded = new ContentType("URL Encoded", 1, 2);
    public static final ContentType MultiPart = new ContentType("Multi-Part", 2, 4);
    public static final ContentType Xml = new ContentType("XML", 3, 8);
    public static final ContentType Json = new ContentType("JSON", 4, 16);
    public static final ContentType Amf = new ContentType("AMF", 5, 32);
    public static final ContentType Unknown = new ContentType("Unknown", -1, 64);

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
    private final int[] ids;
    private final int flags;

    private ContentType() {
        this(None.names[0], None.ids[0], None.flags);
    }

    private ContentType(String name, int id, int flags) {
        this.names = new String[] { name };
        this.ids = new int[] { id };
        this.flags = flags;
    }

    private ContentType(int flags) {
        List<ContentType> contentTypes = getValues().stream()
                .filter(contentType -> contentType.hasFlags(flags))
                .collect(Collectors.toList());
        this.names = contentTypes.stream()
                .map(ContentType::getName).toArray(String[]::new);
        this.ids = contentTypes.stream().mapToInt(ContentType::getId).toArray();
        this.flags = flags;
    }

    public String getName() {
        return String.join(", ", names);
    }

    private int getId() {
        return ids.length == 1 ? ids[0] : Unknown.getId();
    }

    public static ContentType get(burp.api.montoya.http.ContentType contentType) {
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

    public static ContentType get(int id) {
        return getValues().stream()
                .filter(contentType -> contentType.getId() == id)
                .findFirst()
                .orElse(Unknown);
    }

    public boolean isTextBased() {
        return flags != 0 && (flags & ~(Json.flags | UrlEncoded.flags | Xml.flags)) == 0;
    }

    public static List<ContentType> getValues() {
        return values;
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
