package synfron.reshaper.burp.ui.models.rules.whens;

import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.whens.*;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;

import java.util.List;

public class WhenModelType<P extends WhenModel<P, T>, T extends When<T>> extends RuleOperationModelType<P, T> {
    public static final WhenModelType<WhenEventDirectionModel, WhenEventDirection> EventDirection = new WhenModelType<>("Event Direction", WhenEventDirectionModel.class, WhenType.EventDirection);
    public static final WhenModelType<WhenHasEntityModel, WhenHasEntity> HasEntity = new WhenModelType<>("Has Entity", WhenHasEntityModel.class, WhenType.HasEntity);
    public static final WhenModelType<WhenMatchesTextModel, WhenMatchesText> MatchesText = new WhenModelType<>("Matches Text", WhenMatchesTextModel.class, WhenType.MatchesText);
    public static final WhenModelType<WhenContentTypeModel, WhenContentType> ContentType = new WhenModelType<>("Content Type", WhenContentTypeModel.class, WhenType.ContentType);
    public static final WhenModelType<WhenMimeTypeModel, WhenMimeType> MimeType = new WhenModelType<>("MIME Type", WhenMimeTypeModel.class, WhenType.MimeType);
    public static final WhenModelType<WhenProxyNameModel, WhenProxyName> ProxyName = new WhenModelType<>("Proxy Name", WhenProxyNameModel.class, WhenType.ProxyName);
    public static final WhenModelType<WhenFromToolModel, WhenFromTool> FromTool = new WhenModelType<>("From Tool", WhenFromToolModel.class, WhenType.FromTool);
    public static final WhenModelType<WhenInScopeModel, WhenInScope> InScope = new WhenModelType<>("In Scope", WhenInScopeModel.class, WhenType.InScope);

    private WhenModelType(String name, Class<P> type, RuleOperationType<T> ruleOperationType) {
        super(name, type, ruleOperationType);
    }

    public static List<WhenModelType<?,?>> getTypes() {
        return List.of(
                EventDirection,
                HasEntity,
                MatchesText,
                ContentType,
                MimeType,
                ProxyName,
                FromTool,
                InScope
        );
    }
}
