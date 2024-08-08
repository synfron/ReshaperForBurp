package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import lombok.Getter;
import synfron.reshaper.burp.core.HttpConnector;
import synfron.reshaper.burp.core.WebSocketConnector;
import synfron.reshaper.burp.core.events.CollectionChangedAction;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.settings.Workspace;
import synfron.reshaper.burp.core.settings.Workspaces;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.ReshaperComponent;
import synfron.reshaper.burp.ui.utils.ContextMenuHandler;

public class BurpExtender implements BurpExtension, ExtensionUnloadingHandler {

    @Getter
    private static MontoyaApi api;
    private static final ContextMenuHandler contextMenuHandler = new ContextMenuHandler();
    private IEventListener<CollectionChangedArgs> onWorkspacesChangedListener = this::onWorkspacesChanged;

    @Override
    public void initialize(MontoyaApi api) {
        try {
            BurpExtender.api = api;

            api.extension().setName("Reshaper");
            api.userInterface().registerSuiteTab("Reshaper", new ReshaperComponent(Workspaces.get()));
            Workspaces.get().getCollectionChangedEvent().add(onWorkspacesChangedListener);
            Workspaces.get().getWorkspaces().forEach(this::registerWorkspace);

            api.extension().registerUnloadingHandler(this);
            api.userInterface().registerContextMenuItemsProvider(contextMenuHandler);
            Log.getSystem().withMessage("Reshaper started").log();

        } catch (Exception e) {
            Log.getSystem().withMessage("Reshaper startup failed").withException(e).logErr();
            throw  e;
        }
    }

    private void registerWorkspace(Workspace workspace) {
        HttpConnector httpConnector = workspace.getHttpConnector();
        WebSocketConnector webSocketConnector = workspace.getWebSocketConnector();

        workspace.load();
        api.proxy().registerRequestHandler(httpConnector);
        api.proxy().registerResponseHandler(httpConnector);
        api.proxy().registerWebSocketCreationHandler(webSocketConnector);
        api.http().registerHttpHandler(httpConnector);
        api.http().registerSessionHandlingAction(httpConnector);
        api.websockets().registerWebSocketCreatedHandler(webSocketConnector);
    }

    @Override
    public void extensionUnloaded() {
        Workspaces.get().unload();
        Log.get().withMessage("Reshaper unloaded").log();
    }

    private void onWorkspacesChanged(CollectionChangedArgs collectionChangedArgs) {
        if (collectionChangedArgs.getAction() == CollectionChangedAction.Add) {
            registerWorkspace((Workspace) collectionChangedArgs.getItem());
        }
    }
}
