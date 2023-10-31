package synfron.reshaper.burp.core.messages;

import lombok.Getter;

public enum HighlightColor {
    None(null, burp.api.montoya.core.HighlightColor.NONE),
    Red("red", burp.api.montoya.core.HighlightColor.RED),
    Orange("orange", burp.api.montoya.core.HighlightColor.ORANGE),
    Yellow("yellow", burp.api.montoya.core.HighlightColor.YELLOW),
    Green("green", burp.api.montoya.core.HighlightColor.GREEN),
    Cyan("cyan", burp.api.montoya.core.HighlightColor.CYAN),
    Blue("blue", burp.api.montoya.core.HighlightColor.BLUE),
    Pink("pink", burp.api.montoya.core.HighlightColor.PINK),
    Magenta("magenta", burp.api.montoya.core.HighlightColor.MAGENTA),
    Gray("gray", burp.api.montoya.core.HighlightColor.GRAY);

    private final String value;

    @Getter
    private final burp.api.montoya.core.HighlightColor highlightColor;

    HighlightColor(String value, burp.api.montoya.core.HighlightColor highlightColor) {
        this.value = value;
        this.highlightColor = highlightColor;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
