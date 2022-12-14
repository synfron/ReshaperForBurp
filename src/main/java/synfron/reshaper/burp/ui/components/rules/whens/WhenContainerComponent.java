package synfron.reshaper.burp.ui.components.rules.whens;

import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.ui.components.rules.RuleOperationContainerComponent;
import synfron.reshaper.burp.ui.models.rules.RuleOperationModelType;
import synfron.reshaper.burp.ui.models.rules.whens.WhenModelType;

import java.util.HashMap;
import java.util.Map;

public class WhenContainerComponent extends RuleOperationContainerComponent {

    private final static Map<RuleOperationModelType<?,?>, Class<?>> componentMap;

    static {
        componentMap = new HashMap<>();
        componentMap.put(WhenModelType.EventDirection, WhenEventDirectionComponent.class);
        componentMap.put(WhenModelType.WebSocketEventDirection, WhenWebSocketEventDirectionComponent.class);
        componentMap.put(WhenModelType.HasEntity, WhenHasEntityComponent.class);
        componentMap.put(WhenModelType.MatchesText, WhenMatchesTextComponent.class);
        componentMap.put(WhenModelType.ContentType, WhenContentTypeComponent.class);
        componentMap.put(WhenModelType.MimeType, WhenMimeTypeComponent.class);
        componentMap.put(WhenModelType.MessageType, WhenMessageTypeComponent.class);
        componentMap.put(WhenModelType.ProxyName, WhenProxyNameComponent.class);
        componentMap.put(WhenModelType.FromTool, WhenFromToolComponent.class);
        componentMap.put(WhenModelType.InScope, WhenInScopeComponent.class);
    }

    public WhenContainerComponent(ProtocolType protocolType) {
        super(protocolType);
    }

    @Override
    protected Map<RuleOperationModelType<?,?>, Class<?>> getComponentMap() {
        return componentMap;
    }
}
