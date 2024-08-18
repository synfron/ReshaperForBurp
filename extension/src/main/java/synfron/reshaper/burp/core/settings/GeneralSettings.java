package synfron.reshaper.burp.core.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.WorkspaceTab;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.events.PropertyChangedEvent;
import synfron.reshaper.burp.core.messages.Encoder;

import java.util.HashSet;

@Data
public class GeneralSettings {
    private boolean captureProxy = true;
    private boolean captureTarget;
    private boolean captureScanner;
    private boolean captureRepeater;
    private boolean captureIntruder;
    private boolean captureExtender;
    private boolean captureSession = true;
    private boolean captureWebSockets = true;
    private boolean enableEventDiagnostics;
    private int diagnosticValueMaxLength = 200;
    private boolean enableSanityCheckWarnings = true;
    private boolean logInExtenderOutput = false;
    private int logTabCharacterLimit = 1000000;
    private String defaultEncoding = Encoder.getDefaultEncoderName();
    private ImportMethod importMethod = ImportMethod.File;
    private ExportMethod exportMethod = ExportMethod.Json;
    private String importUrl;
    private String lastExportPath;
    private String lastExportFileName = "ReshaperBackup.json";
    private HashSet<String> hiddenThenTypes = new HashSet<>();
    private HashSet<String> hiddenWhenTypes = new HashSet<>();
    private HashSet<WorkspaceTab> hiddenTabs = new HashSet<>();

    @Getter @JsonIgnore
    private final PropertyChangedEvent propertyChangedEvent = new PropertyChangedEvent();


    public void importSettings(GeneralSettings other) {
        if (other != null) {
            this.captureProxy = other.captureProxy;
            this.captureTarget = other.captureTarget;
            this.captureScanner = other.captureScanner;
            this.captureRepeater = other.captureRepeater;
            this.captureIntruder = other.captureIntruder;
            this.captureExtender = other.captureExtender;
            this.captureWebSockets = other.captureWebSockets;
            this.captureSession = other.captureSession;
            this.enableEventDiagnostics = other.enableEventDiagnostics;
            this.diagnosticValueMaxLength = other.diagnosticValueMaxLength;
            this.enableSanityCheckWarnings = other.enableSanityCheckWarnings;
            this.logInExtenderOutput = other.logInExtenderOutput;
            this.logTabCharacterLimit = other.logTabCharacterLimit;
            this.defaultEncoding = other.defaultEncoding;
            this.importMethod = other.importMethod;
            this.importUrl = other.importUrl;
            this.lastExportPath = other.lastExportPath;
            this.lastExportFileName = other.lastExportFileName;
            this.hiddenThenTypes = other.hiddenThenTypes;
            this.hiddenWhenTypes = other.hiddenWhenTypes;
            this.hiddenTabs = other.hiddenTabs;
        }
    }

    public boolean isCapture(BurpTool burpTool) {
        return switch (burpTool) {
            case Proxy -> isCaptureProxy();
            case Repeater -> isCaptureRepeater();
            case Target -> isCaptureTarget();
            case Scanner -> isCaptureScanner();
            case Intruder -> isCaptureIntruder();
            case Extender -> isCaptureExtender();
            case Session -> isCaptureSession();
            case WebSockets -> isCaptureWebSockets();
        };
    }

    private void propertyChanged(String name, Object value) {
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, name, value));
    }

    public void setLogInExtenderOutput(boolean logInExtenderOutput) {
        this.logInExtenderOutput = logInExtenderOutput;
        propertyChanged("logInExtenderOutput", logInExtenderOutput);
    }

    public void setCaptureProxy(boolean captureProxy) {
        this.captureProxy = captureProxy;
        propertyChanged("captureProxy", captureProxy);
    }

    public void setCaptureTarget(boolean captureTarget) {
        this.captureTarget = captureTarget;
        propertyChanged("captureTarget", captureTarget);
    }

    public void setCaptureScanner(boolean captureScanner) {
        this.captureScanner = captureScanner;
        propertyChanged("captureScanner", captureScanner);
    }

    public void setCaptureRepeater(boolean captureRepeater) {
        this.captureRepeater = captureRepeater;
        propertyChanged("captureRepeater", captureRepeater);
    }

    public void setCaptureIntruder(boolean captureIntruder) {
        this.captureIntruder = captureIntruder;
        propertyChanged("captureIntruder", captureIntruder);
    }

    public void setCaptureExtender(boolean captureExtender) {
        this.captureExtender = captureExtender;
        propertyChanged("captureExtender", captureExtender);
    }

    public void setCaptureSession(boolean captureSession) {
        this.captureSession = captureSession;
        propertyChanged("captureSession", captureSession);
    }

    public void setCaptureWebSockets(boolean captureWebSockets) {
        this.captureWebSockets = captureWebSockets;
        propertyChanged("captureWebSockets", captureWebSockets);
    }

    public void setEnableEventDiagnostics(boolean enableEventDiagnostics) {
        this.enableEventDiagnostics = enableEventDiagnostics;
        propertyChanged("enableEventDiagnostics", enableEventDiagnostics);
    }

    public void setDiagnosticValueMaxLength(int diagnosticValueMaxLength) {
        this.diagnosticValueMaxLength = diagnosticValueMaxLength;
        propertyChanged("diagnosticValueMaxLength", diagnosticValueMaxLength);
    }

    public void setEnableSanityCheckWarnings(boolean enableSanityCheckWarnings) {
        this.enableSanityCheckWarnings = enableSanityCheckWarnings;
        propertyChanged("enableSanityCheckWarnings", enableSanityCheckWarnings);
    }

    public void setLogTabCharacterLimit(int logTabCharacterLimit) {
        this.logTabCharacterLimit = logTabCharacterLimit;
        propertyChanged("logTabCharacterLimit", logTabCharacterLimit);
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
        propertyChanged("defaultEncoding", defaultEncoding);
    }

    public void setImportMethod(ImportMethod importMethod) {
        this.importMethod = importMethod;
        propertyChanged("importMethod", importMethod);
    }

    public void setExportMethod(ExportMethod exportMethod) {
        this.exportMethod = exportMethod;
        propertyChanged("exportMethod", exportMethod);
    }

    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
        propertyChanged("importUrl", importUrl);
    }

    public void setLastExportPath(String lastExportPath) {
        this.lastExportPath = lastExportPath;
        propertyChanged("lastExportPath", lastExportPath);
    }

    public void setLastExportFileName(String lastExportFileName) {
        this.lastExportFileName = lastExportFileName;
        propertyChanged("lastExportFileName", lastExportFileName);
    }

    public void setHiddenThenTypes(HashSet<String> hiddenThenTypes) {
        this.hiddenThenTypes = hiddenThenTypes;
        propertyChanged("hiddenThenTypes", hiddenThenTypes);
    }

    public void setHiddenWhenTypes(HashSet<String> hiddenWhenTypes) {
        this.hiddenWhenTypes = hiddenWhenTypes;
        propertyChanged("hiddenWhenTypes", hiddenWhenTypes);
    }

    public void setHiddenTabs(HashSet<WorkspaceTab> hiddenTabs) {
        this.hiddenTabs = hiddenTabs;
        propertyChanged("hiddenTabs", hiddenTabs);
    }

    public GeneralSettings withListener(IEventListener<PropertyChangedArgs> listener) {
        propertyChangedEvent.add(listener);
        return this;
    }

    public enum ImportMethod {
        File,
        Url
    }

    public enum ExportMethod {
        Json,
        Yaml
    }
}

