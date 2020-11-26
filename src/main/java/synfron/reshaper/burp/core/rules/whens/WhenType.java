package synfron.reshaper.burp.core.rules.whens;

import com.fasterxml.jackson.annotation.JsonCreator;
import synfron.reshaper.burp.core.rules.RuleOperationType;

import java.util.List;

public class WhenType<T extends When<T>> extends RuleOperationType<T> {
    public static final WhenType<WhenEventDirection> EventDirection = new WhenType<>("Event Direction", WhenEventDirection.class);
    public static final WhenType<WhenHasEntity> HasEntity = new WhenType<>("Has Entity", WhenHasEntity.class);
    public static final WhenType<WhenMatchesText> MatchesText = new WhenType<>("Matches Text", WhenMatchesText.class);
    public static final WhenType<WhenProxyName> ProxyName = new WhenType<>("Proxy Name", WhenProxyName.class);

    @JsonCreator
    private WhenType(String name, Class<T> type) {
        super(name, type);
    }

    public static List<WhenType<?>> getTypes() {
        return List.of(
                EventDirection,
                HasEntity,
                MatchesText,
                ProxyName
        );
    }
}
