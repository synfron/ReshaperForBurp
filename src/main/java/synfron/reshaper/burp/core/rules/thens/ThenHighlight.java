package synfron.reshaper.burp.core.rules.thens;

import lombok.Getter;
import lombok.Setter;
import synfron.reshaper.burp.core.messages.EventInfo;
import synfron.reshaper.burp.core.rules.RuleOperationType;
import synfron.reshaper.burp.core.rules.RuleResponse;

public class ThenHighlight extends Then<ThenHighlight> {
    @Getter
    @Setter
    private HighlightColor color = HighlightColor.None;

    @Override
    public RuleResponse perform(EventInfo eventInfo) {
        eventInfo.getRequestResponse().setHighlight(color.getValue());
        return RuleResponse.Continue;
    }

    @Override
    public RuleOperationType<ThenHighlight> getType() {
        return ThenType.Highlight;
    }

    public enum HighlightColor {
        None(null),
        Red("red"),
        Orange("orange"),
        Yellow("yellow"),
        Green("green"),
        Cyan("cyan"),
        Blue("blue"),
        Pink("pink"),
        Magenta("magenta"),
        Gray("gray");

        private final String value;

        HighlightColor(String value) {
            this.value = value;
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
