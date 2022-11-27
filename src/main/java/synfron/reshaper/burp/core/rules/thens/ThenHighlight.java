package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.IEventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenHighlight extends Then<ThenHighlight> {
    @Getter
    @Setter
    private HighlightColor color = HighlightColor.None;

    @Override
    public RuleResponse perform(IEventInfo eventInfo) {
        eventInfo.getAnnotations().withHighlightColor(color.highlightColor);
        if (eventInfo.getDiagnostics().isEnabled()) eventInfo.getDiagnostics().logValue(this, false, color.getValue());
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenHighlight> getType() {
        return ThenType.Highlight;
    }

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
}
