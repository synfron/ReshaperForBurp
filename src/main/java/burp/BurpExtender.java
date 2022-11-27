package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import lombok.Getter;
import synfron.reshaper.burp.core.Connector;
import synfron.reshaper.burp.core.events.MessageEvent;
import synfron.reshaper.burp.core.settings.GeneralSettings;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.ReshaperComponent;
import synfron.reshaper.burp.ui.utils.ContextMenuHandler;
import synfron.reshaper.burp.ui.utils.UiMessageHandler;

public class BurpExtender implements BurpExtension {

    @Getter
    private static MontoyaApi api;
    @Getter
    private final static Connector connector = new Connector();
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
        BurpExtender.api = api;

        logTextEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);

        connector.init();

        api.extension().setName("Reshaper");
        api.userInterface().registerSuiteTab("Reshaper", new ReshaperComponent());

        api.proxy().registerRequestHandler(connector);
        api.proxy().registerResponseHandler(connector);
        api.http().registerHttpHandler(connector);
        api.extension().registerUnloadingHandler(connector);
        api.http().registerSessionHandlingAction(connector);
        api.userInterface().registerContextMenuItemsProvider(contextMenuHandler);

        Log.get().withMessage("Reshaper started").log();
    }
}
