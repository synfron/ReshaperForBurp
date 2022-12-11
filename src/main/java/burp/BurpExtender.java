package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import lombok.Getter;
import synfron.reshaper.burp.core.HttpConnector;
import synfron.reshaper.burp.core.ProtocolType;
import synfron.reshaper.burp.core.WebSocketConnector;
import synfron.reshaper.burp.core.events.MessageEvent;
import synfron.reshaper.burp.core.rules.RulesRegistry;
import synfron.reshaper.burp.core.settings.GeneralSettings;
import synfron.reshaper.burp.core.settings.SettingsManager;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.ReshaperComponent;
import synfron.reshaper.burp.ui.utils.ContextMenuHandler;
import synfron.reshaper.burp.ui.utils.UiMessageHandler;

public class BurpExtender implements BurpExtension, ExtensionUnloadingHandler {

    @Getter
    private static MontoyaApi api;
    @Getter
    private final static HttpConnector httpConnector = new HttpConnector();
    @Getter
    private final static WebSocketConnector webSocketConnector = new WebSocketConnector();
    @Getter
    private static RawEditor logTextEditor;
    @Getter
    private static final GeneralSettings generalSettings = new GeneralSettings();
    @Getter
    private static final MessageEvent messageEvent = new MessageEvent();
    private static final UiMessageHandler uiMessageHandler = new UiMessageHandler(messageEvent);
    private static final ContextMenuHandler contextMenuHandler = new ContextMenuHandler();

    @Override
    public void initialize(MontoyaApi api) {
        try {
            BurpExtender.api = api;

            logTextEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);

            SettingsManager.loadSettings();

            httpConnector.init();

            api.extension().setName("Reshaper");
            api.userInterface().registerSuiteTab("Reshaper", new ReshaperComponent());

            api.proxy().registerRequestHandler(httpConnector);
            api.proxy().registerResponseHandler(httpConnector);
            api.proxy().registerWebSocketCreationHandler(webSocketConnector);
            api.http().registerHttpHandler(httpConnector);
            api.http().registerSessionHandlingAction(httpConnector);
            api.extension().registerUnloadingHandler(this);
            api.websockets().registerWebSocketCreationHandler(webSocketConnector);
            api.userInterface().registerContextMenuItemsProvider(contextMenuHandler);
            Log.get().withMessage("Reshaper started").log();

        } catch (Exception e) {
            Log.get().withMessage("Reshaper startup failed").withException(e).logErr();
            throw  e;
        }
    }

    @Override
    public void extensionUnloaded() {
        httpConnector.unload();
        SettingsManager.saveSettings();
        Log.get().withMessage("Reshaper unloaded").log();
    }

    public static RulesRegistry getRulesRegistry(ProtocolType protocolType) {
        return switch (protocolType) {
            case Http -> httpConnector.getRulesEngine().getRulesRegistry();
            case WebSocket -> webSocketConnector.getRulesEngine().getRulesRegistry();
            case Any -> throw new IllegalArgumentException(protocolType + " not valid in this context");
        };
    }
}
