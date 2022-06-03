package synfron.reshaper.burp.core.settings;

import lombok.Data;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.messages.Encoder;

@Data
public class GeneralSettings {
    private boolean captureProxy = true;
    private boolean captureTarget;
    private boolean captureSpider;
    private boolean captureScanner;
    private boolean captureRepeater;
    private boolean captureIntruder;
    private boolean captureExtender;
    private boolean enableEventDiagnostics;
    private int diagnosticValueMaxLength = 200;
    private String RemoteImportAddress = "https://github.com/synfron/ReshaperForBurp";
    private boolean enableSanityCheckWarnings = true;
    private boolean logInExtenderOutput = true;
    private int logTabCharacterLimit = 1000000;
    private String defaultEncoding = Encoder.getDefaultEncoderName();

    public void importSettings(GeneralSettings other) {
        if (other != null) {
            this.captureProxy = other.captureProxy;
            this.captureTarget = other.captureTarget;
            this.captureSpider = other.captureSpider;
            this.captureScanner = other.captureScanner;
            this.captureRepeater = other.captureRepeater;
            this.captureIntruder = other.captureIntruder;
            this.captureExtender = other.captureExtender;
            this.enableEventDiagnostics = other.enableEventDiagnostics;
            this.diagnosticValueMaxLength = other.diagnosticValueMaxLength;
            this.enableSanityCheckWarnings = other.enableSanityCheckWarnings;
            this.logInExtenderOutput = other.logInExtenderOutput;
            this.logTabCharacterLimit = other.logTabCharacterLimit;
            this.RemoteImportAddress = other.RemoteImportAddress;
        }
    }

    public boolean isCapture(BurpTool burpTool) {
        return switch (burpTool) {
            case Proxy -> isCaptureProxy();
            case Repeater -> isCaptureRepeater();
            case Target -> isCaptureTarget();
            case Spider -> isCaptureSpider();
            case Scanner -> isCaptureScanner();
            case Intruder -> isCaptureIntruder();
            case Extender -> isCaptureExtender();
            case Session -> true;
        };
    }
}

