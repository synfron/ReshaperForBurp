package synfron.reshaper.burp.core;

import burp.IBurpExtenderCallbacks;
import lombok.Getter;

import java.util.Arrays;

public enum BurpTool {
    Proxy(IBurpExtenderCallbacks.TOOL_PROXY),
    Repeater(IBurpExtenderCallbacks.TOOL_REPEATER),
    Intruder(IBurpExtenderCallbacks.TOOL_INTRUDER),
    Target(IBurpExtenderCallbacks.TOOL_TARGET),
    Spider(IBurpExtenderCallbacks.TOOL_SPIDER),
    Scanner(IBurpExtenderCallbacks.TOOL_SCANNER),
    Extender(IBurpExtenderCallbacks.TOOL_EXTENDER),
    Session(null);

    @Getter
    private final Integer id;

    BurpTool(Integer id) {
        this.id = id;
    }

    public static BurpTool getById(int id) {
        return Arrays.stream(values()).filter(tool -> tool.getId() == id).findFirst().orElse(null);
    }
}
