package synfron.reshaper.burp.core.settings;

import lombok.Data;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.messages.Encoder;

@Data
public class GeneralSettings {
    private boolean captureProxy = true;
    private boolean captureTarget;
    private boolean captureScanner;
    private boolean captureRepeater;
    private boolean captureIntruder;
    private boolean captureExtender;
    private boolean captureWebSockets = true;
    private boolean enableEventDiagnostics;
    private int diagnosticValueMaxLength = 200;
    private boolean enableSanityCheckWarnings = true;
    private boolean logInExtenderOutput = true;
    private int logTabCharacterLimit = 1000000;
    private String defaultEncoding = Encoder.getDefaultEncoderName();
    private ImportMethod importMethod = ImportMethod.File;
    private ExportMethod exportMethod = ExportMethod.Json;
    private String importUrl;

    public void importSettings(GeneralSettings other) {
        if (other != null) {
            this.captureProxy = other.captureProxy;
            this.captureTarget = other.captureTarget;
            this.captureScanner = other.captureScanner;
            this.captureRepeater = other.captureRepeater;
            this.captureIntruder = other.captureIntruder;
            this.captureExtender = other.captureExtender;
            this.captureWebSockets = other.captureWebSockets;
            this.enableEventDiagnostics = other.enableEventDiagnostics;
            this.diagnosticValueMaxLength = other.diagnosticValueMaxLength;
            this.enableSanityCheckWarnings = other.enableSanityCheckWarnings;
            this.logInExtenderOutput = other.logInExtenderOutput;
            this.logTabCharacterLimit = other.logTabCharacterLimit;
            this.importMethod = other.importMethod;
            this.importUrl = other.importUrl;
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
            case Session -> true;
            case WebSockets -> isCaptureWebSockets();
        };
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

