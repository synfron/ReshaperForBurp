package synfron.reshaper.burp.core.settings;

import lombok.Data;
import synfron.reshaper.burp.core.BurpTool;

@Data
public class GeneralSettings {
    private boolean captureProxy = true;
    private boolean captureTarget;
    private boolean captureSpider;
    private boolean captureScanner;
    private boolean captureRepeater;
    private boolean captureIntruder;
    private boolean captureExtender;

    public void importSettings(GeneralSettings other) {
        if (other != null) {
            this.captureProxy = other.captureProxy;
            this.captureTarget = other.captureTarget;
            this.captureSpider = other.captureSpider;
            this.captureScanner = other.captureScanner;
            this.captureRepeater = other.captureRepeater;
            this.captureIntruder = other.captureIntruder;
            this.captureExtender = other.captureExtender;
        }
    }

    public boolean isCapture(BurpTool burpTool) {
        switch (burpTool) {
            case Proxy:
                return isCaptureProxy();
            case Repeater:
                return isCaptureRepeater();
            case Target:
                return isCaptureTarget();
            case Spider:
                return isCaptureSpider();
            case Scanner:
                return isCaptureScanner();
            case Intruder:
                return isCaptureIntruder();
            case Extender:
                return isCaptureExtender();
        }
        return false;
    }
}
