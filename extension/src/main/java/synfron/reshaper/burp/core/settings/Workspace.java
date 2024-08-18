package synfron.reshaper.burp.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.HttpConnector;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.WebSocketConnector;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.MessageEvent;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.vars.GlobalVariables;

import java.util.UUID;

public class Workspace {
    @Getter
    private final GeneralSettings generalSettings = new GeneralSettings();
    @Getter
    private final GlobalVariables globalVariables = new GlobalVariables();
    @Getter
    private final HttpConnector httpConnector = new HttpConnector(this);
    @Getter
    private final WebSocketConnector webSocketConnector = new WebSocketConnector(this);
    @Getter @Setter
    private boolean defaultWorkspace = false;
    @Getter
    private int version = 1;
    @Getter @Setter
    private boolean legacyLoad = false;
    @Getter
    private String workspaceName;
    @Getter
    private final UUID workspaceUuid;
    @Getter
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();
    @Getter
    private final MessageEvent messageEvent = new MessageEvent();

    public Workspace(@JsonProperty UUID workspaceUuid, @JsonProperty String workspaceName) {
        this.workspaceUuid = workspaceUuid;
        this.workspaceName = workspaceName;
    }

    public RulesRegistry getRulesRegistry(ProtocolType protocolType) {
        return switch (protocolType) {
            case Http -> httpConnector.getRulesEngine().getRulesRegistry();
            case WebSocket -> webSocketConnector.getRulesEngine().getRulesRegistry();
            case Any -> throw new IllegalArgumentException(protocolType + " not valid in this context");
        };
    }

    public RulesRegistry getHttpRulesRegistry() {
        return getRulesRegistry(ProtocolType.Http);
    }

    public RulesRegistry getWebSocketRulesRegistry() {
        return getRulesRegistry(ProtocolType.WebSocket);
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
        propertyChanged("workspaceName", workspaceName);
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public Workspace withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public void unload() {
        httpConnector.unload();
        webSocketConnector.unload();
    }

    public void load() {
        httpConnector.init();
    }
}
