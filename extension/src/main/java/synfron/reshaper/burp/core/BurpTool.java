package synfron.reshaper.burp.core;

import burp.api.montoya.core.ToolType;

public enum BurpTool {
    Proxy,
    Repeater,
    Intruder,
    Target,
    Scanner,
    Extender("Extensions"),
    Session,
    WebSockets;

    private final String displayName;

    BurpTool() {
        displayName = name();
    }

    BurpTool(String displayName) {
        this.displayName = displayName;
    }

    public static BurpTool from(ToolType toolType) {
        return switch (toolType) {
            case PROXY -> Proxy;
            case TARGET -> Target;
            case SCANNER -> Scanner;
            case INTRUDER -> Intruder;
            case REPEATER -> Repeater;
            case EXTENSIONS -> Extender;
            case SUITE, SEQUENCER, RECORDED_LOGIN_REPLAYER, COMPARER, DECODER, LOGGER, ORGANIZER -> null;
        };
    }


    @Override
    public String toString() {
        return displayName;
    }
}
