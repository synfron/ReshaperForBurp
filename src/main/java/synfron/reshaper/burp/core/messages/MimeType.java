package synfron.reshaper.burp.core.messages;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class MimeType {
    public static final MimeType Html = new MimeType("HTML", 1);
    public static final MimeType Script = new MimeType("Script", 2);
    public static final MimeType Css = new MimeType("CSS", 4);
    public static final MimeType Json = new MimeType("JSON", 8);
    public static final MimeType Svg = new MimeType("SVG", 16);
    public static final MimeType OtherXml = new MimeType("Other XML", 32);
    public static final MimeType OtherText = new MimeType("Other Text", 64);
    public static final MimeType Image = new MimeType("Image", 128);
    public static final MimeType OtherBinary = new MimeType("Other Binary", 256);
    public static final MimeType Unknown = new MimeType("Unknown", 512);

    private static final List<MimeType> values = List.of(
            Html,
            Script,
            Css,
            Json,
            Svg,
            OtherXml,
            OtherText,
            Image,
            OtherBinary,
            Unknown
    );

    @Getter
    private final String[] names;
    private final int flags;

    private MimeType() {
        this(Html.names[0], Html.flags);
    }

    private MimeType(String[] names, int flags) {
        this.names = names;
        this.flags = flags;
    }

    private MimeType(String name, int flags) {
        this.names = new String[] { name };
        this.flags = flags;
    }

    private MimeType(int flags) {
        this(getValues().stream()
                .filter(mimeType -> mimeType.hasFlags(flags))
                .map(MimeType::getName)
                .collect(Collectors.joining(", ")), flags);
    }

    public String getName() {
        return String.join(", ", names);
    }

    public static MimeType get(burp.api.montoya.http.message.MimeType mimeType) {
        return switch (mimeType) {
            case HTML -> Html;
            case SCRIPT -> Script;
            case CSS -> Css;
            case JSON -> Json;
            case IMAGE_SVG_XML -> Svg;
            case XML -> OtherXml;
            case PLAIN_TEXT, YAML, RTF -> OtherText;
            case IMAGE_UNKNOWN, IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF, IMAGE_BMP, IMAGE_TIFF -> Image;
            case APPLICATION_FLASH, APPLICATION_UNKNOWN, LEGACY_SER_AMF, SOUND, VIDEO -> OtherBinary;
            default -> Unknown;
        };
    }

    public boolean isTextBased() {
        return flags < MimeType.Image.flags;
    }

    public static List<MimeType> getValues() {
        return values;
    }

    public boolean hasFlags(MimeType mimeType) {
        return hasFlags(mimeType.flags);
    }

    private boolean hasFlags(int flags) {
        return (this.flags & flags) == flags;
    }

    public MimeType or(MimeType mimeType) {
        int newFlag = flags | mimeType.flags;
        return new MimeType(newFlag);
    }

    public MimeType minus(MimeType mimeType) {
        int newFlag = flags - mimeType.flags;
        return new MimeType(newFlag);
    }

    @Override
    public String toString() {
        return getName();
    }
}
