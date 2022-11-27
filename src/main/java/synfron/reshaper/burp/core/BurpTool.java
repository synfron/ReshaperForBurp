package synfron.reshaper.burp.core;

import burp.api.montoya.core.ToolType;

public enum BurpTool {
    Proxy,
    Repeater,
    Intruder,
    Target,
    Scanner,
    Extender,
    Session;

    public static BurpTool getBy(ToolType toolType) {
        return switch (toolType) {
            case PROXY -> Proxy;
            case TARGET -> Target;
            case SCANNER -> Scanner;
            case INTRUDER -> Intruder;
            case REPEATER -> Repeater;
            case EXTENSIONS -> Extender;
            case SUITE, SEQUENCER, RECORDED_LOGIN_REPLAYER, COMPARER, DECODER, LOGGER -> null;
        };
    }
}
