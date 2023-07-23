package synfron.reshaper.burp.ui.models.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.whens.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WhenModelType<P extends WhenModel<P, T>, T extends When<T>> extends RuleOperationModelType<P, T> {
    public static final WhenModelType<WhenEventDirectionModel, WhenEventDirection> EventDirection = new WhenModelType<>(WhenEventDirectionModel.class, WhenType.EventDirection);
    public static final WhenModelType<WhenWebSocketEventDirectionModel, WhenWebSocketEventDirection> WebSocketEventDirection = new WhenModelType<>(WhenWebSocketEventDirectionModel.class, WhenType.WebSocketEventDirection);
    public static final WhenModelType<WhenHasEntityModel, WhenHasEntity> HasEntity = new WhenModelType<>(WhenHasEntityModel.class, WhenType.HasEntity);
    public static final WhenModelType<WhenMatchesTextModel, WhenMatchesText> MatchesText = new WhenModelType<>(WhenMatchesTextModel.class, WhenType.MatchesText);
    public static final WhenModelType<WhenContentTypeModel, WhenContentType> ContentType = new WhenModelType<>(WhenContentTypeModel.class, WhenType.ContentType);
    public static final WhenModelType<WhenMimeTypeModel, WhenMimeType> MimeType = new WhenModelType<>(WhenMimeTypeModel.class, WhenType.MimeType);
    public static final WhenModelType<WhenMessageTypeModel, WhenMessageType> MessageType = new WhenModelType<>(WhenMessageTypeModel.class, WhenType.MessageType);
    public static final WhenModelType<WhenProxyNameModel, WhenProxyName> ProxyName = new WhenModelType<>(WhenProxyNameModel.class, WhenType.ProxyName);
    public static final WhenModelType<WhenFromToolModel, WhenFromTool> FromTool = new WhenModelType<>(WhenFromToolModel.class, WhenType.FromTool);
    public static final WhenModelType<WhenInScopeModel, WhenInScope> InScope = new WhenModelType<>(WhenInScopeModel.class, WhenType.InScope);
    public static final WhenModelType<WhenRepeatModel, WhenRepeat> Repeat = new WhenModelType<>(WhenRepeatModel.class, WhenType.Repeat);

    private WhenModelType(Class<P> type, RuleOperationType<T> ruleOperationType) {
        super(type, ruleOperationType);
    }

    public static List<WhenModelType<?,?>> getTypes(ProtocolType protocolType) {
        return Stream.of(
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
        ).filter(type -> type.hasProtocolType(protocolType)).collect(Collectors.toList());
    }
}
