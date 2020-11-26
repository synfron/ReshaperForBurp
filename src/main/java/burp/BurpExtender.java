package burp;

import lombok.Getter;
import synfron.reshaper.burp.core.Connector;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.ui.components.ReshaperComponent;

public class BurpExtender implements IBurpExtender {

    @Getter
    private static IBurpExtenderCallbacks callbacks;
    @Getter
    private final static Connector connector = new Connector();

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        BurpExtender.callbacks = callbacks;
        connector.init();

        callbacks.addSuiteTab(new ReshaperComponent());
        callbacks.setExtensionName("Reshaper");
        callbacks.registerProxyListener(connector);
        callbacks.registerExtensionStateListener(connector);
        Log.get().withMessage("Reshaper started").log();
    }
}
