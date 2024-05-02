package synfron.reshaper.burp.core.rules.whens;

import lombok.EqualsAndHashCode;
import synfron.reshaper.burp.core.rules.RuleOperationType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class WhenType<T extends When<T>> extends RuleOperationType<T> {
    public static final WhenType<WhenEventDirection> EventDirection = new WhenType<>("Event Direction", WhenEventDirection.class);
    public static final WhenType<WhenWebSocketEventDirection> WebSocketEventDirection = new WhenType<>("Event Direction", WhenWebSocketEventDirection.class);
    public static final WhenType<WhenHasEntity> HasEntity = new WhenType<>("Has Entity", WhenHasEntity.class);
    public static final WhenType<WhenMatchesText> MatchesText = new WhenType<>("Matches Text", WhenMatchesText.class);
    public static final WhenType<WhenContentType> ContentType = new WhenType<>("Request Content Type", WhenContentType.class);
    public static final WhenType<WhenMimeType> MimeType = new WhenType<>("Response MIME Type", WhenMimeType.class);
    public static final WhenType<WhenMessageType> MessageType = new WhenType<>("Message Type", WhenMessageType.class);
    public static final WhenType<WhenProxyName> ProxyName = new WhenType<>("Proxy Name", WhenProxyName.class);
    public static final WhenType<WhenFromTool> FromTool = new WhenType<>("From Tool", WhenFromTool.class);
    public static final WhenType<WhenInScope> InScope = new WhenType<>("In Scope", WhenInScope.class);
    public static final WhenType<WhenRepeat> Repeat = new WhenType<>("Repeat", WhenRepeat.class);

    private WhenType() {
        this(null, null);
    }

    private WhenType(String name, Class<T> type) {
        super(name, type);
    }

    public static List<WhenType<?>> getTypes() {
        return List.of(
                EventDirection,
                WebSocketEventDirection,
                HasEntity,
                MatchesText,
                ContentType,
                MimeType,
                MessageType,
                ProxyName,
                FromTool,
                InScope,
                Repeat
        );
    }
}
